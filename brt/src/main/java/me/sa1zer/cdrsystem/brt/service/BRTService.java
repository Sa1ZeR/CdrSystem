package me.sa1zer.cdrsystem.brt.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.sa1zer.cdrsystem.brt.payload.mapper.*;
import me.sa1zer.cdrsystem.common.payload.dto.*;
import me.sa1zer.cdrsystem.common.payload.request.ReportUpdateDataRequest;
import me.sa1zer.cdrsystem.common.payload.response.BillingResponse;
import me.sa1zer.cdrsystem.common.payload.response.CdrResponse;
import me.sa1zer.cdrsystem.common.service.HttpService;
import me.sa1zer.cdrsystem.common.service.KafkaSender;
import me.sa1zer.cdrsystem.commondb.entity.BillingData;
import me.sa1zer.cdrsystem.commondb.entity.ReportData;
import me.sa1zer.cdrsystem.commondb.entity.User;
import me.sa1zer.cdrsystem.commondb.service.BillingDataService;
import me.sa1zer.cdrsystem.commondb.service.OperatorService;
import me.sa1zer.cdrsystem.commondb.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BRTService {

    private static final Map<String, List<CdrPlusDto>> CDR_PLUS_CACHE = new HashMap<>();
    private static final Map<String, List<ReportDto>> REPORT_DATA_CACHE = new HashMap<>();
    private static final Map<String, Double> TOTAL_COST_CACHE = new HashMap<>();

    private final UserService userService;
    private final OperatorService operatorService;
    private final ReportDataMapper reportDataMapper;
    private final ReportDtoMapper reportDtoMapper;
    private final UserMapper userMapper;
    private final BillingMapper billingMapper;
    private final OperatorMapper operatorMapper;
    private final BillingDataService billingDataService;

    private final HttpService httpService;
    private final KafkaSender kafkaSender;

    @Value("${settings.url.cdr-address}")
    private String cdrAddress;

    @Value("${settings.broker.topic.user-update-topic}")
    private String userUpdateTopic;

    @PostConstruct
    public void initService() {
        //updateCDRPlus(false);
        initReportCache();
    }

    @Transactional
    public void updateBalance(String phone, double balance) {
        userService.updateUserBalance(phone, balance);
    }

    public Map<String, List<CdrPlusDto>> getCdrPlusData() {
        return CDR_PLUS_CACHE;
    }

    public List<ReportDto> getReportByPhone(String phone) {
        return REPORT_DATA_CACHE.get(phone);
    }

    public List<User> getUsers(Set<String> phones) {
        return userService.findAllInSet(phones);
    }

    private void updateTotalPrice(String phone, double totalPrice) {
        TOTAL_COST_CACHE.put(phone, totalPrice);
    }

    public double getTotalCost(String phone) {
        return TOTAL_COST_CACHE.getOrDefault(phone, 0D);
    }

    private BillingData addBillingData(String phone, List<ReportDto> reportList, double totalPrice) {
        User user = userService.getUserByPhone(phone);

        Set<ReportData> reportDataList = reportList.stream().map(reportDataMapper::map).collect(Collectors.toSet());

       return BillingData.builder()
                .user(user)
                .reportData(reportDataList)
                .totalCost(totalPrice)
                .build();
    }

    public List<BillingData> findAllBillingData() {
        return billingDataService.findAll();
    }

    private void saveAllBillingData(List<BillingData> reportsToSave) {
        billingDataService.saveAll(reportsToSave);
    }

    public List<UserDto> getAllUsers() {
        List<User> users = userService.findAll();

        return users.stream().map(userMapper::map).collect(Collectors.toList());
    }

    public void updateReports() {
        updateCDRPlus(true);
        //clear old report data in db
        deleteAllBillingData();
        //clear cache
        REPORT_DATA_CACHE.clear();
    }

    private void deleteAllBillingData() {
        billingDataService.deleteAll();
    }

    private void updateCDRPlus(boolean isNew) {
        if(isNew) {
            httpService.sendPatchRequest(cdrAddress + "update", null, String.class);
        }

        ResponseEntity<CdrResponse> response = httpService.
                sendGetRequest(cdrAddress + "getAll", CdrResponse.class);
        if(!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            log.error(String.format("Can't get cdr data. Response code: %s, message: %s",
                    response.getStatusCode(), response));
            return;
        }

        Map<String, List<CdrDto>> cdrData = response.getBody().cdrData();

        //get users with balance > 0 && operator == Ромашка
        List<User> users = userService.findAllWithPositiveBalance(cdrData.keySet(), "Ромашка");

        updateCdrPlus(users, cdrData);
    }

    //create cdr + tariff data
    private void updateCdrPlus(List<User> users, Map<String, List<CdrDto>> cdrData) {
        CDR_PLUS_CACHE.clear();

        for(User u : users) {
            List<CdrDto> cdrDtos = cdrData.get(u.getPhone());

            if(!ObjectUtils.isEmpty(cdrDtos)) {
                for(CdrDto cdrDto : cdrDtos) {
                    List<CdrPlusDto> cdrPlusData = CDR_PLUS_CACHE.getOrDefault(u.getPhone(), new ArrayList<>());
                    cdrPlusData.add(CdrPlusDto.builder()
                            .phoneNumber(u.getPhone())
                            .callType(cdrDto.callType())
                            .startTime(cdrDto.startTime())
                            .endTime(cdrDto.endTime())
                            .tariffType(u.getTariff().getType())
                            .operator(u.getOperator().getName())
                            .build());

                    CDR_PLUS_CACHE.put(u.getPhone(), cdrPlusData);
                }
            }
        }
    }

    public BillingResponse updateReportData(ReportUpdateDataRequest request) {
        List<BillingData> reportsToSave = new LinkedList<>();
        Set<String> updated = new HashSet<>();

        request.data().forEach(d -> {
            updateBalance(d.phone(), -d.totalPrice());
            updateTotalPrice(d.phone(), d.totalPrice()); //update cache
            updated.add(d.phone());

            REPORT_DATA_CACHE.put(d.phone(), d.reports()); //update cache

            kafkaSender.sendMessage(userUpdateTopic, d.phone()); //update user cache

            reportsToSave.add(addBillingData(d.phone(), d.reports(), d.totalPrice()));
        });

        saveAllBillingData(reportsToSave);

        List<BillingDto> dtoList = getUsers(updated).stream().map(billingMapper::map)
                .collect(Collectors.toList());

        return new BillingResponse(dtoList);
    }

    /*  update user in cache*/
    public void updateUserCache(String phoneNumber) {
        userService.updateUserCache(phoneNumber);
    }

    private void initReportCache() {
        List<BillingData> data = billingDataService.findAll();
        data.forEach(d -> {
            TOTAL_COST_CACHE.put(d.getUser().getPhone(), d.getTotalCost());
            List<ReportDto> dtoList = d.getReportData().stream().map(reportDtoMapper::map).collect(Collectors.toList());
            REPORT_DATA_CACHE.put(d.getUser().getPhone(), dtoList);
        });
    }

    public List<OperatorDto> getAllOperators() {
        return operatorService.findAll().stream()
                .map(operatorMapper::map)
                .collect(Collectors.toList());
    }
}
