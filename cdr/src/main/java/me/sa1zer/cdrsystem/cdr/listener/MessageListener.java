package me.sa1zer.cdrsystem.cdr.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.sa1zer.cdrsystem.cdr.service.CDRService;
import me.sa1zer.cdrsystem.common.service.KafkaSender;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
@RequiredArgsConstructor
@Slf4j
public class MessageListener {

    private static final String cdrGEnTopic = "${settings.broker.topic.cdr-gen-topic}";
    private final CDRService cdrService;
    private final KafkaSender kafkaSender;

    @Value("${settings.broker.topic.cdr-plus-gen-topic}")
    private String cdrPlusTopic;

    @KafkaListener(topics = cdrGEnTopic, groupId = "default")
    public void handleMessage(String message) {
        log.info("CDR generation message successfully received");
        cdrService.genCDRFile(true);

        kafkaSender.sendMessage(cdrPlusTopic, "genCdrPlus");
    }
}
