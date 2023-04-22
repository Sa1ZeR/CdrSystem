package me.sa1zer.cdrsystem.hrs.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.sa1zer.cdrsystem.common.payload.response.BillingResponse;
import me.sa1zer.cdrsystem.common.payload.response.ReportUpdateDataResponse;
import me.sa1zer.cdrsystem.common.service.HttpService;
import me.sa1zer.cdrsystem.common.service.KafkaSender;
import me.sa1zer.cdrsystem.hrs.service.HRSService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class MessageListener {

    private static final String launchBillingTopic = "${settings.broker.topic.billing-launch-topic}";

    private final HRSService hrsService;
    private final HttpService httpService;

    @Value("${settings.url.brt-address}")
    private String brtAddress;

    @KafkaListener(topics = launchBillingTopic, groupId = "default")
    public void handleMessage(String message) {
        log.info("Successfully received message to launch billing");
        ReportUpdateDataResponse reportUpdateDataResponse = hrsService.launchBilling();

        httpService.sendPatchRequest(brtAddress + "report/updateBillingData", reportUpdateDataResponse,
                BillingResponse.class);
    }
}
