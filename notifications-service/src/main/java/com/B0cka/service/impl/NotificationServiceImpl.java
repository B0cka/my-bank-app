package com.B0cka.service.impl;

import com.B0cka.dto.NotificationRequestDto;
import com.B0cka.service.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class NotificationServiceImpl implements NotificationService {

    @Override
    public void send(NotificationRequestDto request) {
        log.info("Notification for user [{}]: {}", request.getLogin(), request.getMessage());
    }
}