package com.b0cka.service;

import com.b0cka.common.dto.AccountBalanceOperationRequest;
import com.b0cka.dto.AccountDto;
import com.b0cka.dto.UpdateAccountDto;

import java.util.List;

public interface AccountService {

    AccountDto updateCurrentAccount(UpdateAccountDto updateAccountDto);

    AccountDto getCurrentAccount();

    List<AccountDto> getOtherAccounts();

    void deposit(AccountBalanceOperationRequest accountBalanceOperationRequest);

    void withdraw(AccountBalanceOperationRequest accountBalanceOperationRequest);

}
