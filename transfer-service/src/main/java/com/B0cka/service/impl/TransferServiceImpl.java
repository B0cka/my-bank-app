package com.B0cka.service.impl;

import com.B0cka.clients.AccountsClient;
import com.B0cka.dto.TransferRequest;
import com.B0cka.ex.FundsTransferException;
import com.B0cka.ex.InvalidAmount;
import com.B0cka.kafka.producer.TransferEventProducer;
import com.B0cka.service.TransferService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TransferServiceImpl implements TransferService {

    private final AccountsClient accountsClient;
    private final TransferEventProducer transferEventProducer;

    @Override
    public String transferMoney(TransferRequest transferRequest) {
        String senderLogin = currentUser();

        if (transferRequest.getRecipientLogin().equals(senderLogin)) {
            throw new FundsTransferException("Нельзя переводить самому себе!");
        }

        if (transferRequest.getAmount() <= 0) {
            throw new InvalidAmount("Сумма перевода меньше минимальной!");
        }

        accountsClient.withdraw(senderLogin, transferRequest.getAmount());
        accountsClient.deposit(transferRequest.getRecipientLogin(), transferRequest.getAmount());

        transferEventProducer.sendTransferEvent(
                senderLogin,
                transferRequest.getRecipientLogin(),
                transferRequest.getAmount()
        );

        return "Успешный перевод";
    }

    private String currentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication instanceof JwtAuthenticationToken jwtAuthenticationToken) {
            Jwt jwt = jwtAuthenticationToken.getToken();
            String username = jwt.getClaimAsString("preferred_username");

            if (username != null && !username.isBlank()) {
                return username;
            }
        }

        throw new IllegalStateException("Не удалось определить логин текущего пользователя");
    }
}