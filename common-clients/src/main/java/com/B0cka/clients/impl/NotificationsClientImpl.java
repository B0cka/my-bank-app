package com.B0cka.clients.impl;


import com.B0cka.clients.NotificationsClient;
import com.B0cka.dto.NotificationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
@RequiredArgsConstructor
public class NotificationsClientImpl implements NotificationsClient {

    private final RestClient.Builder restClientBuilder;
    private final OAuth2AuthorizedClientManager authorizedClientManager;

    @Value("${notification.service.path}")
    private String notificationsServicePath;

    @Override
    public void sendNotification(NotificationRequest request) {
        OAuth2AuthorizedClient client = authorizedClientManager.authorize(
                OAuth2AuthorizeRequest.withClientRegistrationId("keycloak")
                        .principal("cash-service")
                        .build()
        );

        if (client == null || client.getAccessToken() == null) {
            throw new IllegalStateException("Не удалось получить access token для notifications-service");
        }

        restClientBuilder.build()
                .post()
                .uri(notificationsServicePath + "/notifications")
                .header("Authorization", "Bearer " + client.getAccessToken().getTokenValue())
                .body(request)
                .retrieve()
                .toBodilessEntity();
    }
}