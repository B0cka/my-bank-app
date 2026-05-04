package com.B0cka.consumer;

import com.B0cka.events.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class NotificationsConsumer {

    @KafkaListener(topics = "bank.events", groupId = "notifications-group")
    public void handle(BankEvent event) {
        switch (event) {
            case MoneyDepositedEvent d -> processDeposit(d);
            case MoneyWithdrawnEvent w -> processWithdrawal(w);
            case MoneyTransferredEvent w -> processTransferred(w);
            case ProfileUpdatedEvent w -> processUpdate(w);
        }
    }

    private void processDeposit(MoneyDepositedEvent moneyDepositedEvent){
        log.info("Received event: {}", moneyDepositedEvent);
    }

    private void processWithdrawal(MoneyWithdrawnEvent moneyWithdrawnEvent){
        log.info("Received event: {}", moneyWithdrawnEvent);
    }

    private void processTransferred(MoneyTransferredEvent event){
        log.info("Received event: {}", event);
    }

    private void processUpdate(ProfileUpdatedEvent event){
        log.info("Received event: {}", event);
    }

}
