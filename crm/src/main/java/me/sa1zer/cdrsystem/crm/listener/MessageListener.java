package me.sa1zer.cdrsystem.crm.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.sa1zer.cdrsystem.commondb.service.UserService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class MessageListener {

    private final UserService userService;

    private static final String userTopic = "${settings.broker.topic.user-update-topic}";

    @KafkaListener(topics = userTopic, groupId = "default")
    public void handleUserMessage(String phoneNumber) {
        log.info("Successfully received message to update user in cache");
        userService.updateUserCache(phoneNumber);
    }
}
