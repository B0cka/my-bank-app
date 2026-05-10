package com.b0cka.consumer;

import com.b0cka.events.*;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class NotificationsConsumer {

    private final MeterRegistry meterRegistry;

    @KafkaListener(topics = "bank.events", groupId = "notifications-group")
    public void handle(BankEvent event) {
        try {
            switch (event) {
                case MoneyDepositedEvent d -> processDeposit(d);
                case MoneyWithdrawnEvent w -> processWithdrawal(w);
                case MoneyTransferredEvent w -> processTransferred(w);
                case ProfileUpdatedEvent w -> processUpdate(w);
            }
        } catch (Exception e) {
            String login = extractLogin(event);
            meterRegistry.counter("bank.notification.failed", "login", login)
                    .increment();
            log.error("Failed to process notification for user: {}", login, e);
        }
    }

    private void processDeposit(MoneyDepositedEvent event) {
        log.info("Sending deposit notification to: {}", event.login());
        sendNotification(event.login(), "Пополнение: " + event.amount());
    }

    private void processWithdrawal(MoneyWithdrawnEvent event) {
        log.info("Sending withdrawal notification to: {}", event.login());
        sendNotification(event.login(), "Снятие: " + event.amount());
    }

    private void processTransferred(MoneyTransferredEvent event) {
        log.info("Sending transfer notification to: {}", event.toLogin());
        sendNotification(event.toLogin(), "Перевод от " + event.fromLogin() + ": " + event.amount());
    }

    private void processUpdate(ProfileUpdatedEvent event) {
        log.info("Sending profile update notification to: {}", event.login());
        sendNotification(event.login(), "Профиль обновлен");
    }

    private void sendNotification(String login, String message) {

        if ("broken".equals(login)) {
            throw new RuntimeException("Simulated notification failure");
        }
        log.info("Notification sent to {}: {}", login, message);
    }


    private String extractLogin(BankEvent event) {
        return switch (event) {
            case MoneyDepositedEvent e -> e.login();
            case MoneyWithdrawnEvent e -> e.login();
            case MoneyTransferredEvent e -> e.toLogin(); // или fromLogin, по ТЗ
            case ProfileUpdatedEvent e -> e.login();
            default -> "unknown";
        };
    }
}
