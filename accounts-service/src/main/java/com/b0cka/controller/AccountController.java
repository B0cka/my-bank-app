package com.b0cka.controller;

import com.b0cka.dto.AccountBalanceOperationRequest;
import com.b0cka.dto.AccountDto;
import com.b0cka.dto.UpdateAccountDto;
import com.b0cka.service.AccountService;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/accounts")
@Slf4j
public class AccountController {

    private final AccountService accountService;

    @Autowired
    private io.micrometer.tracing.Tracer tracer;

    @Autowired
    private ObservationRegistry registry;

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

    @GetMapping("/mdc")
    public String test() {
        log.info("TEST LOG");
        return "Trace: " + tracer.currentSpan().context().traceId();
    }

}
