package me.sa1zer.cdrsystem.brt.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.sa1zer.cdrsystem.brt.service.BRTService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class MessageListener {

    private final BRTService brtService;

    private static final String userTopic = "${settings.broker.topic.user-update-topic}";

    @KafkaListener(topics = userTopic)
    public void handleUserMessage(String phoneNumber) {
        log.info("Successfully received message to update user in cache");
        brtService.updateUserCache(phoneNumber);
    }
}
