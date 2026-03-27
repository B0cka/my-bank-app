package com.b0cka.controller;

import com.b0cka.dto.AccountBalanceOperationRequest;
import com.b0cka.dto.AccountDto;
import com.b0cka.dto.UpdateAccountDto;
import com.b0cka.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/internal/deposit")
    @PreAuthorize("hasRole('SERVICE')")
    public void deposit(@RequestBody AccountBalanceOperationRequest accountBalanceOperationRequest) {

        accountService.deposit(accountBalanceOperationRequest);
    }

    @PostMapping("/internal/withdraw")
    @PreAuthorize("hasRole('SERVICE')")
    public void withdraw(@RequestBody AccountBalanceOperationRequest accountBalanceOperationRequest) {

        accountService.withdraw(accountBalanceOperationRequest);
    }

}
