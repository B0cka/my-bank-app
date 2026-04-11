package com.B0cka.clients;

import com.B0cka.dto.NotificationRequest;

public interface NotificationsClient {
    void sendNotification(NotificationRequest request);
}