package com.b0cka.consumer;

import com.b0cka.events.*;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationsConsumerTest {

    @Mock
    private MeterRegistry meterRegistry;

    @Mock
    private Counter counter;

    @InjectMocks
    private NotificationsConsumer notificationsConsumer;

    @BeforeEach
    void setUp() {
        lenient().when(meterRegistry.counter(anyString(), anyString(), anyString()))
                .thenReturn(counter);
    }

    @Test
    @DisplayName("DEPOSIT event: обрабатывается корректно")
    void handle_depositEvent_success() {
        MoneyDepositedEvent event = new MoneyDepositedEvent(
                "evt-1", "user123", 100L, LocalDateTime.now()
        );

        notificationsConsumer.handle(event);

        verify(meterRegistry, never())
                .counter(eq("bank.notification.failed"), anyString(), anyString());
    }

    @Test
    @DisplayName("WITHDRAW event: обрабатывается корректно")
    void handle_withdrawEvent_success() {
        MoneyWithdrawnEvent event = new MoneyWithdrawnEvent(
                "evt-2", "user456", 50L, LocalDateTime.now()
        );

        notificationsConsumer.handle(event);

        verify(meterRegistry, never())
                .counter(eq("bank.notification.failed"), anyString(), anyString());
    }

    @Test
    @DisplayName("TRANSFER event: обрабатывается корректно")
    void handle_transferEvent_success() {
        MoneyTransferredEvent event = new MoneyTransferredEvent(
                "evt-3", "sender", "receiver", 200L, LocalDateTime.now()
        );

        notificationsConsumer.handle(event);

        verify(meterRegistry, never())
                .counter(eq("bank.notification.failed"), anyString(), anyString());
    }

    @Test
    @DisplayName("PROFILE_UPDATE event: обрабатывается корректно")
    void handle_profileUpdateEvent_success() {
        ProfileUpdatedEvent event = new ProfileUpdatedEvent(
                "evt-4", "user789", "New Name",
                LocalDate.of(2000, 1, 1),
                LocalDateTime.now()
        );

        notificationsConsumer.handle(event);

        verify(meterRegistry, never())
                .counter(eq("bank.notification.failed"), anyString(), anyString());
    }

    @Test
    @DisplayName("Broken login: логирует ошибку, инкрементит метрику, НЕ падает")
    void handle_brokenLogin_logsError_incrementsMetric_doesNotThrow() {
        MoneyDepositedEvent event = new MoneyDepositedEvent(
                "evt-5", "broken", 100L, LocalDateTime.now()
        );

        assertDoesNotThrow(() -> notificationsConsumer.handle(event));

        verify(meterRegistry).counter(eq("bank.notification.failed"), eq("login"), eq("broken"));
        verify(counter).increment();
    }

    @Test
    @DisplayName("TRANSFER event с broken toLogin: инкрементит метрику с toLogin")
    void handle_transferEvent_withBrokenToLogin_incrementsMetricWithToLogin() {
        MoneyTransferredEvent event = new MoneyTransferredEvent(
                "evt-6", "sender", "broken", 200L, LocalDateTime.now()
        );

        assertDoesNotThrow(() -> notificationsConsumer.handle(event));

        verify(meterRegistry).counter(eq("bank.notification.failed"), eq("login"), eq("broken"));
        verify(counter).increment();
    }
}