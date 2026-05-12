package com.b0cka.kafka.producer.impl;

import com.b0cka.events.MoneyDepositedEvent;
import com.b0cka.events.MoneyWithdrawnEvent;
import com.b0cka.kafka.producer.CashEventProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CashEventProducerImpl implements CashEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    public void sendDepositEvent(String login, Long amount) {
        MoneyDepositedEvent event = new MoneyDepositedEvent(
                UUID.randomUUID().toString(),
                login,
                amount,
                LocalDateTime.now()
        );

        kafkaTemplate.send("bank.events", login, event);
    }

    @Override
    public void sendWithdrawEvent(String login, Long amount) {
        MoneyWithdrawnEvent event = new MoneyWithdrawnEvent(
                UUID.randomUUID().toString(),
                login,
                amount,
                LocalDateTime.now()
        );

        kafkaTemplate.send("bank.events", login, event);
    }
}