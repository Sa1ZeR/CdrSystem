package me.sa1zer.cdrsystem.common.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaSender {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public void sendMessage(String topic, String message) {
        kafkaTemplate.send(topic, message);
        log.info("Successfully send message to {} topic", topic);
    }
}
