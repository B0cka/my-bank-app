package com.B0cka.service.impl;

import com.B0cka.clients.AccountsClient;
import com.B0cka.dto.TransferRequest;
import com.B0cka.ex.FundsTransferException;
import com.B0cka.ex.InvalidAmount;
import com.B0cka.service.TransferService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class TransferServiceImpl implements TransferService {

    private final AccountsClient accountsClient;

    @Override
    public String transferMoney(TransferRequest transferRequest) {

        if(transferRequest.getRecipientLogin().equals(currentUser())){
            throw new FundsTransferException("Нельзя переводить самому себе!");
        }

        if(transferRequest.getAmount() <= 0){
            throw new InvalidAmount("Сумма перевода меньше минимальной!");
        }
            accountsClient.withdraw(currentUser(), transferRequest.getAmount());
            accountsClient.deposit(transferRequest.getRecipientLogin(), transferRequest.getAmount());
            return "Успешный перевод";

    }

    private String currentUser() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

}
