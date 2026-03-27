package com.B0cka.service.impl;

import com.B0cka.clients.AccountsClient;
import com.B0cka.dto.CashAction;
import com.B0cka.ex.InvalidAmount;
import com.B0cka.ex.InvalidAction;
import com.B0cka.service.CashService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
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
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }


}
