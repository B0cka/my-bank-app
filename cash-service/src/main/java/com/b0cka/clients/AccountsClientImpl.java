package com.b0cka.clients;

import com.b0cka.dto.AccountBalanceOperationRequest;
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
public class AccountsClientImpl implements AccountsClient {

    private final RestClient.Builder restClientBuilder;
    private final OAuth2AuthorizedClientManager authorizedClientManager;

    @Value("${account.service.path}")
    private String accountServicePath;

    @Override
    public void deposit(String login, Long amount) {
        String accessToken = getAccessToken();

        restClientBuilder.build()
                .post()
                .uri(accountServicePath + "/accounts/internal/deposit")
                .header("Authorization", "Bearer " + accessToken)
                .body(AccountBalanceOperationRequest.builder()
                        .login(login)
                        .amount(amount)
                        .build())
                .retrieve()
                .toBodilessEntity();
    }

    @Override
    public void withdraw(String login, Long amount) {
        String accessToken = getAccessToken();

        restClientBuilder.build()
                .post()
                .uri(accountServicePath + "/accounts/internal/withdraw")
                .header("Authorization", "Bearer " + accessToken)
                .body(AccountBalanceOperationRequest.builder()
                        .login(login)
                        .amount(amount)
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
            throw new IllegalStateException("Не удалось получить access token для межсервисного вызова");
        }

        return authorizedClient.getAccessToken().getTokenValue();
    }
}