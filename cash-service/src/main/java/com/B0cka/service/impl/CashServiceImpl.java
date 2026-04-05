package com.B0cka.service.impl;

import com.B0cka.clients.AccountsClient;
import com.B0cka.dto.CashAction;
import com.B0cka.ex.InvalidAmount;
import com.B0cka.ex.InvalidAction;
import com.B0cka.service.CashService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CashServiceImpl implements CashService {

    private final AccountsClient accountsClient;

    public String performCashOperation(CashAction cashAction, Long sum) {

        if(sum <= 0){
            throw new InvalidAmount("Сумма должна быть больше нуля!");
        }

        if(cashAction == CashAction.DEPOSIT){
            accountsClient.deposit(currentUser(), sum);

        }else if (cashAction == CashAction.WITHDRAW){
            accountsClient.withdraw(currentUser(), sum);
        }else{
            throw new InvalidAction("Неизвестное действие");
        }

        return "Успешно";
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
