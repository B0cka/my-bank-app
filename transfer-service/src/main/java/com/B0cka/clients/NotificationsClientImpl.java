package com.B0cka.clients;

import com.B0cka.dto.NotificationRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
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
    private String notificationServicePath;

    @Override
    public void send(String login, String message) {
        String accessToken = getAccessToken();

        restClientBuilder.build()
                .post()
                .uri(notificationServicePath + "/notifications")
                .header("Authorization", "Bearer " + accessToken)
                .body(NotificationRequestDto.builder()
                        .login(login)
                        .message(message)
                        .build())
                .retrieve()
                .toBodilessEntity();
    }

    private String getAccessToken() {
        OAuth2AuthorizeRequest authorizeRequest = OAuth2AuthorizeRequest
                .withClientRegistrationId("keycloak")
                .principal(new AnonymousAuthenticationToken(
                        "system",
                        "system",
                        AuthorityUtils.createAuthorityList("ROLE_SYSTEM")
                ))
                .build();

        OAuth2AuthorizedClient authorizedClient = authorizedClientManager.authorize(authorizeRequest);

        if (authorizedClient == null || authorizedClient.getAccessToken() == null) {
            throw new IllegalStateException("Не удалось получить access token для вызова notifications-service");
        }

        return authorizedClient.getAccessToken().getTokenValue();
    }
}