package com.b0cka.service;

import com.b0cka.dto.AccountDto;
import com.b0cka.dto.CashAction;
import com.b0cka.dto.UpdateAccountDto;

public interface AccountService {

    AccountDto updateCurrentAccount(UpdateAccountDto updateAccountDto);

    AccountDto getCurrentAccount();

}
