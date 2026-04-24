package com.B0cka.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class NotificationsConsumer {

    @KafkaListener(topics = "bank.events", groupId = "notifications-group")
    public void handle(Object event) {
        log.info("Received event: {}", event);
    }

}
