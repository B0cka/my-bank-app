package com.B0cka.events;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record ProfileUpdatedEvent(
        String eventId,
        String login,
        String name,
        LocalDate birthday,
        LocalDateTime occurredAt
) {}