package com.b0cka.kafka.producer.impl;

import com.b0cka.events.ProfileUpdatedEvent;
import com.b0cka.kafka.producer.NotificationClientProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationClientProducerImpl implements NotificationClientProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    public void sendToNotificationService(ProfileUpdatedEvent profileUpdatedEvent) {

        try {
            kafkaTemplate.send(
                    "bank.events",
                    profileUpdatedEvent.login(),
                    profileUpdatedEvent
            );
        }catch (Exception e){
            log.error("Error sending log events", e);
        }

    }
}
