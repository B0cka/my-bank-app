package com.B0cka.events;

import java.time.LocalDateTime;

public record MoneyDepositedEvent(
        String eventId,
        String login,
        Long amount,
        LocalDateTime occurredAt
) {}