package com.b0cka.service.impl;

import com.b0cka.clients.AccountsClient;
import com.b0cka.dto.TransferRequest;
import com.b0cka.ex.FundsTransferException;
import com.b0cka.ex.InvalidAmount;
import com.b0cka.kafka.producer.TransferEventProducer;
import com.b0cka.service.TransferService;
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
            meterRegistry.counter("bank.transfer.failed", "reason", "self_transfer").increment();
            throw new FundsTransferException("Нельзя переводить самому себе!");
        }

        if (amount <= 0) {
            meterRegistry.counter("bank.transfer.failed", "reason", "invalid_amount").increment();
            throw new InvalidAmount("Сумма перевода меньше минимальной!");
        }

        try {
            accountsClient.withdraw(senderLogin, amount);
            try {
                accountsClient.deposit(recipientLogin, amount);

                transferEventProducer.sendTransferEvent(senderLogin, recipientLogin, amount);

                meterRegistry.counter("bank.transfer.success", "operation", "transfer").increment();
                return "Успешный перевод";

            } catch (Exception depositEx) {
                log.error("Deposit failed for {}. Attempting compensation refund to {}", recipientLogin, senderLogin, depositEx);
                try {
                    accountsClient.deposit(senderLogin, amount);
                    log.info("Compensation successful. Refunded {} to {}", amount, senderLogin);
                    meterRegistry.counter("bank.transfer.compensated", "operation", "refund").increment();
                } catch (Exception compensationEx) {
                    log.error("CRITICAL: Compensation FAILED! Manual intervention required for {} to refund {}",
                            senderLogin, amount, compensationEx);
                    meterRegistry.counter("bank.transfer.compensation_failed").increment();
                }

                meterRegistry.counter("bank.transfer.failed", "reason", "deposit_error").increment();
                throw new FundsTransferException("Ошибка при зачислении. Перевод отменен: " + depositEx.getMessage());
            }

        } catch (FundsTransferException | InvalidAmount e) {
            throw e;
        } catch (Exception withdrawEx) {
            meterRegistry.counter("bank.transfer.failed", "reason", "withdraw_error").increment();
            log.error("Withdraw failed for {}: {}", senderLogin, withdrawEx.getMessage(), withdrawEx);
            throw new FundsTransferException("Ошибка при списании средств: " + withdrawEx.getMessage());
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