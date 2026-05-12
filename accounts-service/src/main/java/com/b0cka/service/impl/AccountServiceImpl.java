package com.b0cka.service.impl;

import com.b0cka.component.AccountMapper;
import com.b0cka.common.dto.AccountBalanceOperationRequest;
import com.b0cka.dto.AccountDto;
import com.b0cka.dto.UpdateAccountDto;
import com.b0cka.entity.Account;
import com.b0cka.ex.NotEnoughException;
import com.b0cka.ex.NotFoundException;
import com.b0cka.ex.YoungUserException;
import com.b0cka.repository.AccountRepository;
import com.b0cka.service.AccountService;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final MeterRegistry meterRegistry;

    @Override
    public List<AccountDto> getOtherAccounts() {
        String currentLogin = currentUser();
        log.info("Fetching other accounts for user: {}", currentLogin);

        List<AccountDto> others = accountRepository.findAll().stream()
                .filter(account -> !account.getLogin().equals(currentLogin))
                .map(AccountMapper::toDto)
                .toList();

        log.debug("Found {} other accounts for user: {}", others.size(), currentLogin);
        return others;
    }

    @Override
    public AccountDto updateCurrentAccount(UpdateAccountDto updateAccountDto) {
        String currentLogin = currentUser();
        log.info("Updating account for user: {}, dto: {}", currentLogin, updateAccountDto);

        if (ChronoUnit.YEARS.between(updateAccountDto.getBirthday(), LocalDate.now()) < 18) {
            log.warn("Registration attempt for underage user: {}", currentLogin);
            throw new YoungUserException("Возраст юзера не соответствует требованиям банка");
        }

        Account account = accountRepository.findByLogin(currentLogin).orElseGet(() -> {
            log.info("Creating new account for user: {}", currentLogin);
            return Account.builder()
                    .name("Client" + (Math.random() * 900000) + 100000)
                    .login(currentLogin)
                    .balance(0L)
                    .birthday(LocalDate.of(2000, 12, 1))
                    .build();
        });

        account.setName(updateAccountDto.getName());
        account.setBirthday(updateAccountDto.getBirthday());
        accountRepository.save(account);

        log.info("Successfully updated account for user: {}, accountId: {}", currentLogin, account.getId());
        return AccountMapper.toDto(account);
    }

    @Override
    public AccountDto getCurrentAccount() {
        String currentLogin = currentUser();
        log.debug("Fetching current account for user: {}", currentLogin);

        Account account = accountRepository.findByLogin(currentLogin)
                .orElseGet(() -> {
                    log.info("Account not found for user: {}, creating new one", currentLogin);
                    return accountRepository.save(Account.builder()
                            .name("Client" + (Math.random() * 900000) + 100000)
                            .login(currentLogin)
                            .balance(0L)
                            .birthday(LocalDate.of(2000, 12, 1))
                            .build());
                });

        log.debug("Returning account details for user: {}", currentLogin);
        return AccountMapper.toDto(account);
    }

    @Override
    @Transactional
    public void deposit(AccountBalanceOperationRequest request) {
        String login = request.getLogin();
        long amount = request.getAmount();
        log.info("Deposit request for user: {}, amount: {}", login, amount);

        Account account = accountRepository.findByLogin(login).orElseThrow(() -> {
            log.error("Deposit failed: account not found for login: {}", login);
            return new NotFoundException("Аккаунт не найден");
        });

        account.setBalance(account.getBalance() + amount);
        accountRepository.save(account);

        log.info("Successfully deposited {} to account of user: {}, new balance: {}", amount, login, account.getBalance());
    }

    @Override
    @Transactional
    public void withdraw(AccountBalanceOperationRequest request) {
        String login = request.getLogin();
        long amount = request.getAmount();
        log.info("Withdrawal request for user: {}, amount: {}", login, amount);

        Account account = accountRepository.findByLogin(login).orElseThrow(() -> {
            log.error("Withdrawal failed: account not found for login: {}", login);
            return new NotFoundException("Аккаунт не найден");
        });

        if (account.getBalance() < amount) {
            log.warn("Withdrawal rejected: insufficient funds for user: {}, requested: {}, available: {}",
                    login, amount, account.getBalance());
            meterRegistry.counter("bank.withdrawal.failed", "reason", "insufficient_funds").increment();
            throw new NotEnoughException("Недостаточно средств на счету");
        }

        account.setBalance(account.getBalance() - amount);
        accountRepository.save(account);

        log.info("Successfully withdrew {} from account of user: {}, new balance: {}", amount, login, account.getBalance());
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
        return authentication.getName();
    }
}