package com.B0cka.dto;

import java.time.LocalDateTime;

public record NotificationRequestDto(
        String eventId,
        String login,
        Long amount,
        LocalDateTime occurredAt
) {}