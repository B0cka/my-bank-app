package com.b0cka.controller;

import com.b0cka.dto.AccountBalanceOperationRequest;
import com.b0cka.dto.AccountDto;
import com.b0cka.dto.UpdateAccountDto;
import com.b0cka.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/accounts")
public class AccountController {

    private final AccountService accountService;

    @PostMapping("/me")
    public AccountDto updateCurrentAccount(@RequestBody @Valid UpdateAccountDto updateAccountDto) {
        return accountService.updateCurrentAccount(updateAccountDto);
    }

    @GetMapping("/me")
    public AccountDto getCurrentAccount() {
        return accountService.getCurrentAccount();
    }

    @GetMapping("/others")
    public List<AccountDto> getOtherAccounts() {
        return accountService.getOtherAccounts();
    }

    @Transactional
    @PostMapping("/internal/deposit")
    public void deposit(@RequestBody AccountBalanceOperationRequest accountBalanceOperationRequest) {

        accountService.deposit(accountBalanceOperationRequest);
    }

    @Transactional
    @PostMapping("/internal/withdraw")
    public void withdraw(@RequestBody AccountBalanceOperationRequest accountBalanceOperationRequest) {

        accountService.withdraw(accountBalanceOperationRequest);
    }

}
