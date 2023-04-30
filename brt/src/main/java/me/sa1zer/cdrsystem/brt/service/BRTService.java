package me.sa1zer.cdrsystem.brt.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.sa1zer.cdrsystem.brt.payload.mapper.BillingMapper;
import me.sa1zer.cdrsystem.brt.payload.mapper.ReportDataMapper;
import me.sa1zer.cdrsystem.brt.payload.mapper.ReportDtoMapper;
import me.sa1zer.cdrsystem.brt.payload.mapper.UserMapper;
import me.sa1zer.cdrsystem.common.object.enums.ActionType;
import me.sa1zer.cdrsystem.common.payload.dto.BillingDto;
import me.sa1zer.cdrsystem.common.payload.dto.ReportDto;
import me.sa1zer.cdrsystem.common.payload.dto.UserDto;
import me.sa1zer.cdrsystem.common.payload.request.BillingRequest;
import me.sa1zer.cdrsystem.common.payload.response.BillingResponse;
import me.sa1zer.cdrsystem.common.payload.response.ReportUpdateDataResponse;
import me.sa1zer.cdrsystem.common.service.HttpService;
import me.sa1zer.cdrsystem.common.service.KafkaSender;
import me.sa1zer.cdrsystem.commondb.entity.BillingData;
import me.sa1zer.cdrsystem.commondb.entity.ReportData;
import me.sa1zer.cdrsystem.commondb.entity.User;
import me.sa1zer.cdrsystem.commondb.service.BillingDataService;
import me.sa1zer.cdrsystem.commondb.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BRTService {

    private static final Map<String, List<ReportDto>> REPORT_DATA_CACHE = new HashMap<>();
    private static final Map<String, Double> TOTAL_COST_CACHE = new HashMap<>();

    private final UserService userService;
    private final ReportDataMapper reportDataMapper;
    private final ReportDtoMapper reportDtoMapper;
    private final UserMapper userMapper;
    private final BillingMapper billingMapper;
    private final BillingDataService billingDataService;
    private final CdrPlusService cdrPlusService;

    private final HttpService httpService;
    private final KafkaSender kafkaSender;

    @Value("${settings.url.hsr-address}")
    private String hsrAddress;

    @Value("${settings.broker.topic.cdr-gen-topic}")
    private String cdrGenTopic;
    @Value("${settings.broker.topic.user-update-topic}")
    private String userUpdateTopic;

    @PostConstruct
    public void initService() {
        initReportCache();
    }

    private void initReportCache() {
        List<BillingData> data = billingDataService.findAll();
        if(data.size() == 0) //if data == 0, cdr we must launch auto billing
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    kafkaSender.sendMessage(cdrGenTopic, "genCdr");
                }
            }, 10000L);

        data.forEach(d -> {
            TOTAL_COST_CACHE.put(d.getUser().getPhone(), d.getTotalCost());
            List<ReportDto> dtoList = d.getReportData().stream().map(reportDtoMapper::map).collect(Collectors.toList());
            REPORT_DATA_CACHE.put(d.getUser().getPhone(), dtoList);
        });
    }

    public List<ReportDto> getReportByPhone(String phone) {
        return REPORT_DATA_CACHE.get(phone);
    }

    public List<UserDto> getAllUsers() {
        List<User> users = userService.findAll();

        return users.stream().map(userMapper::map)
                .collect(Collectors.toList());
    }

    public double getTotalCost(String phone) {
        return TOTAL_COST_CACHE.getOrDefault(phone, 0D);
    }

    private void updateTotalPrice(String phone, double totalPrice) {
        TOTAL_COST_CACHE.put(phone, totalPrice);
    }

    private void clearOldData() {
        //clear old report data in db
        billingDataService.deleteAll();
        //clear cache
        REPORT_DATA_CACHE.clear();
    }

    /*  update user in cache*/
    public void updateUserCache(String phoneNumber) {
        userService.updateUserCache(phoneNumber);
    }

    /**
     * В данный момент взаимодействие сделано при помощи отправки запросов на соответствующие
     * ендпоинты микросервисов, изначально планировалось сделать при помощи отправки dto'шек через кафку, но в рамках
     * тз надо работать с файлами. Следующим решением было отправлять файлы через RestTemplate(multipart-form-data),
     * но после прочтения некоторых статей, оказалось, что не рекомендуется отправлять большие файлы таким способом.
     * Затем думал решить задачу нотификацией через кафку, но мне нужно получать ответ о завершении какого-либо действия,
     * а кафка это больше для асинхронного взаимодействия, поэтому в конечном счете я пришел к решению, которое будет по
     * нужным ендпоинтам проводить определенные действия на микросервисах. При первом запуске о генерации файлов и тарификации,
     * сервисы получают информацию при помощи нотификации (используется кафка)
     * @param request
     * @return users which updated by billing
     */
    public BillingResponse billing(BillingRequest request) {
        ActionType type = ActionType.getType(request.action());
        if(type != ActionType.RUN)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad action type");

        if(!ObjectUtils.isEmpty(request.clearOld()) && request.clearOld())
            //clear old report data if needed
            clearOldData();

        //send to cdr service (we must create new cdr.txt) and then we must create cdr+
        cdrPlusService.updateCDRPlus(true);

        //send request to brt, that cdr+ was created, and he must launch billing process
        ReportUpdateDataResponse response = httpService.sendPatchRequest(hsrAddress + "/billing", request, ReportUpdateDataResponse.class);

        //save user changes in db and return response to crm
        return updateReportData(response);
    }

    //update users after billing process
    @Transactional
    public BillingResponse updateReportData(ReportUpdateDataResponse request) {
        List<BillingData> reportsToSave = new LinkedList<>();
        Set<String> updated = new HashSet<>();

        request.data().forEach(d -> {
            if(d.totalPrice() > 0) {
                userService.updateUserBalance(d.phone(), -d.totalPrice());
                updateTotalPrice(d.phone(), d.totalPrice()); //update cache
            }
            updated.add(d.phone());

            REPORT_DATA_CACHE.put(d.phone(), d.reports()); //update cache

            kafkaSender.sendMessage(userUpdateTopic, d.phone()); //update user cache

            reportsToSave.add(createBillingData(d.phone(), d.reports(), d.totalPrice()));
        });

        billingDataService.saveAll(reportsToSave);

        List<BillingDto> dtoList = userService.findAllInSet(updated).stream().map(billingMapper::map)
                .collect(Collectors.toList());

        return new BillingResponse(dtoList);
    }

    //create billing data for user (billing data - information about user's calls with costs)
    private BillingData createBillingData(String phone, List<ReportDto> reportList, double totalPrice) {
        User user = userService.getUserByPhone(phone);

        Set<ReportData> reportDataList = reportList.stream().map(reportDataMapper::map)
                .collect(Collectors.toSet());

        return BillingData.builder()
                .user(user)
                .reportData(reportDataList)
                .totalCost(totalPrice)
                .build();
    }
}
