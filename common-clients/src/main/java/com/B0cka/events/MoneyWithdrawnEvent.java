package com.B0cka.events;

import java.time.LocalDateTime;

public record MoneyWithdrawnEvent(
        String eventId,
        String login,
        Long amount,
        LocalDateTime occurredAt
) {}