package com.b0cka.service;

import com.b0cka.dto.AccountBalanceOperationRequest;
import com.b0cka.dto.AccountDto;
import com.b0cka.dto.UpdateAccountDto;

public interface AccountService {

    AccountDto updateCurrentAccount(UpdateAccountDto updateAccountDto);

    AccountDto getCurrentAccount();

    void deposit(AccountBalanceOperationRequest accountBalanceOperationRequest);

    void withdraw(AccountBalanceOperationRequest accountBalanceOperationRequest);

}
