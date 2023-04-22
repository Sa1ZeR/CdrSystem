package me.sa1zer.cdrsystem.brt.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.sa1zer.cdrsystem.brt.service.BRTService;
import me.sa1zer.cdrsystem.brt.service.CdrPlusService;
import me.sa1zer.cdrsystem.common.payload.response.ReportUpdateDataResponse;
import me.sa1zer.cdrsystem.common.service.KafkaSender;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class MessageListener {

    private static final String userTopic = "${settings.broker.topic.user-update-topic}";
    private static final String cdrPlusTopic = "${settings.broker.topic.cdr-plus-gen-topic}";
    private static final String reportUpdateTopic = "${settings.broker.topic.update-report-data-topic}";

    private final BRTService brtService;
    private final CdrPlusService cdrPlusService;
    private final KafkaSender kafkaSender;

    @Value("${settings.broker.topic.billing-launch-topic}")
    private String billingLaunchTopic;

    @KafkaListener(topics = userTopic)
    public void handleUserMessage(String phoneNumber) {
        log.info("Successfully received message to update user in cache");
        brtService.updateUserCache(phoneNumber);
    }

    @KafkaListener(topics = cdrPlusTopic)
    public void handleCdrMessage(String message) {
        log.info("Successfully received message to gen cdr+");
        cdrPlusService.updateCDRPlus(false);

        kafkaSender.sendMessage(billingLaunchTopic, "launch");
    }

    @KafkaListener(topics = reportUpdateTopic)
    public void handleReportUpdateMessage(ReportUpdateDataResponse message) {
        log.info("Successfully received message to update report");
        brtService.updateReportData(message);
    }
}
