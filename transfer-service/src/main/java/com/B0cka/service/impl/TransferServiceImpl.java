package com.B0cka.service.impl;

import com.B0cka.clients.AccountsClient;
import com.B0cka.dto.TransferRequest;
import com.B0cka.ex.FundsTransferException;
import com.B0cka.ex.InvalidAmount;
import com.B0cka.kafka.producer.TransferEventProducer;
import com.B0cka.service.TransferService;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransferServiceImpl implements TransferService {

    private final AccountsClient accountsClient;
    private final TransferEventProducer transferEventProducer;
    private final MeterRegistry meterRegistry;

    @Override
    public String transferMoney(TransferRequest transferRequest) {
        String senderLogin = currentUser();
        String recipientLogin = transferRequest.getRecipientLogin();
        long amount = transferRequest.getAmount();

        if (recipientLogin.equals(senderLogin)) {
            meterRegistry.counter("bank.transfer.failed", "from", senderLogin, "to", recipientLogin).increment();
            throw new FundsTransferException("Нельзя переводить самому себе!");
        }

        if (amount <= 0) {
            meterRegistry.counter("bank.transfer.failed", "from", senderLogin, "to", recipientLogin).increment();
            throw new InvalidAmount("Сумма перевода меньше минимальной!");
        }
        try {
            accountsClient.withdraw(senderLogin, amount);
            accountsClient.deposit(recipientLogin, amount);
            transferEventProducer.sendTransferEvent(senderLogin, recipientLogin, amount);

            return "Успешный перевод";

        } catch (Exception e) {

            meterRegistry.counter("bank.transfer.failed", "from", senderLogin, "to", recipientLogin)
                    .increment();
            log.error("Transfer failed for {} -> {}: {}", senderLogin, recipientLogin, e.getMessage(), e);
            throw new FundsTransferException("Ошибка при выполнении перевода", e);
        }
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