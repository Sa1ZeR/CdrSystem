package me.sa1zer.cdrsystem.crm.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.sa1zer.cdrsystem.common.payload.dto.ReportDto;
import me.sa1zer.cdrsystem.common.object.enums.OperationType;
import me.sa1zer.cdrsystem.common.payload.response.PhoneReportResponse;
import me.sa1zer.cdrsystem.common.service.HttpService;
import me.sa1zer.cdrsystem.common.service.KafkaSender;
import me.sa1zer.cdrsystem.commondb.entity.Payment;
import me.sa1zer.cdrsystem.commondb.entity.User;
import me.sa1zer.cdrsystem.common.object.enums.UserRole;
import me.sa1zer.cdrsystem.commondb.service.PaymentService;
import me.sa1zer.cdrsystem.commondb.service.UserService;
import me.sa1zer.cdrsystem.crm.payload.mapper.PayUserMapper;
import me.sa1zer.cdrsystem.common.payload.mapper.ReportMapper;
import me.sa1zer.cdrsystem.crm.payload.request.PayRequest;
import me.sa1zer.cdrsystem.crm.payload.response.AbonentReportResponse;
import me.sa1zer.cdrsystem.crm.payload.response.PayUserResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class AbonentService {

    private final UserService userService;
    private final PaymentService paymentService;
    private final KafkaSender kafkaSender;

    private final PayUserMapper payUserMapper;
    private final ReportMapper reportMapper;
    private final HttpService httpService;

    @Value("${settings.url.brt-address}")
    private String brtUrl;

    @Value("${settings.broker.topic.user-update-topic}")
    private String userUpdateTopic;
    @Transactional
    public ResponseEntity<?> payMoney(PayRequest request) {
        User searched = userService.getUserByPhone(request.numberPhone());
        searched.setBalance(searched.getBalance() + request.money());

        //create payment data in db
        Payment payment = Payment.builder()
                .amount(request.money())
                .user(searched)
                .operationType(OperationType.REFILL)
                .dateTime(LocalDateTime.now())
                .build();

        payment = paymentService.save(payment);
        userService.save(searched);

        PayUserResponse response = payUserMapper.map(searched);
        response.setId(payment.getId());
        response.setMoney(payment.getAmount());

        //send message to kafka for update user data
        kafkaSender.sendMessage(userUpdateTopic, searched.getPhone());

        return ResponseEntity.ok(response);
    }

    public ResponseEntity<?> getReport(String numberPhone, Principal principal) {
        User user = userService.getUserByPhone(principal.getName());
        User searched = userService.getUserByPhone(numberPhone);
        if(!numberPhone.equals(user.getPhone()) && !user.getRoles().contains(UserRole.MANAGER))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "You are don't have permissions to get information about another phone number");

        //send request to brt because he's storing information about billing (report data)
        PhoneReportResponse responseByPhone = httpService.sendGetRequest(String.format(
                brtUrl + "/report/getReportByPhone/%s", numberPhone), PhoneReportResponse.class).getBody();
        List<ReportDto> reportByPhone = responseByPhone.reports()
                .stream().sorted(Comparator.comparing(ReportDto::startTime)).toList();

        //create response
        AbonentReportResponse reportResponse = AbonentReportResponse.builder()
                .id(searched.getId())
                .numberPhone(searched.getPhone())
                .tariffIndex(searched.getTariff().getCode())
                .currentBalance(searched.getBalance())
                .monetaryUnit("rubles")
                .payload(reportByPhone == null ? null: reportByPhone.stream().map(reportMapper::map).collect(Collectors.toList()))
                .totalCost(responseByPhone.totalCost())
                .build();

        return ResponseEntity.ok(reportResponse);
    }
}
