package com.B0cka.service;


import com.B0cka.dto.NotificationRequestDto;

public interface NotificationService {
    void send(NotificationRequestDto request);
}