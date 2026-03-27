package com.B0cka.clients;

import com.B0cka.dto.AccountBalanceOperationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServletOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
public class AccountsClientImpl implements AccountsClient {

    private final WebClient.Builder web;
    private final OAuth2AuthorizedClientManager authorizedClientManager;

    @Value("${account.service.path}")
    private String ACCOUNT_SERVICE_URL_PATH;

    @Override
    public void deposit(String login, Long amount) {
        var oauth2 = new ServletOAuth2AuthorizedClientExchangeFilterFunction(authorizedClientManager);
        oauth2.setDefaultClientRegistrationId("cash-id");

        web.filter(oauth2)
                .build()
                .post()
                .uri(ACCOUNT_SERVICE_URL_PATH + "/accounts/internal/deposit")
                .bodyValue(AccountBalanceOperationRequest.builder()
                        .amount(amount)
                        .login(login)
                        .build())
                .retrieve()
                .toBodilessEntity()
                .block();

    }

    @Override
    public void withdraw(String login, Long amount) {
        var oauth2 = new ServletOAuth2AuthorizedClientExchangeFilterFunction(authorizedClientManager);
        oauth2.setDefaultClientRegistrationId("cash-id");
        web.filter(oauth2)
                .build()
                .post()
                .uri(ACCOUNT_SERVICE_URL_PATH + "/accounts/internal/withdraw")
                .bodyValue(AccountBalanceOperationRequest.builder()
                        .amount(amount)
                        .login(login)
                        .build())
                .retrieve()
                .toBodilessEntity()
                .block();
    }


}
