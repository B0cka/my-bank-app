package com.b0cka.kafka.producer.impl;

import com.b0cka.events.MoneyTransferredEvent;
import com.b0cka.kafka.producer.TransferEventProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransferEventProducerImpl implements TransferEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    public void sendTransferEvent(String from, String to, Long amount) {

        MoneyTransferredEvent event = new MoneyTransferredEvent(
                UUID.randomUUID().toString(),
                from,
                to,
                amount,
                LocalDateTime.now()
        );

        kafkaTemplate.send("bank.events", from, event);
    }
}