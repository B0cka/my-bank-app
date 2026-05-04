package com.B0cka.events;

import java.time.LocalDateTime;

public record MoneyTransferredEvent(
        String eventId,
        String fromLogin,
        String toLogin,
        Long amount,
        LocalDateTime occurredAt
) implements BankEvent {}