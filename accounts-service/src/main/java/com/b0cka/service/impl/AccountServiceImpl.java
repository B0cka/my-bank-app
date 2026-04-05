package com.b0cka.service.impl;

import com.b0cka.component.AccountMapper;
import com.b0cka.dto.AccountBalanceOperationRequest;
import com.b0cka.dto.AccountDto;
import com.b0cka.dto.UpdateAccountDto;
import com.b0cka.entity.Account;
import com.b0cka.ex.InvalidLoginException;
import com.b0cka.ex.NotEnoughException;
import com.b0cka.ex.NotFoundException;
import com.b0cka.ex.YoungUserException;
import com.b0cka.repository.AccountRepository;
import com.b0cka.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;

    @Override
    public List<AccountDto> getOtherAccounts() {
        String currentLogin = currentUser();

        return accountRepository.findAll().stream()
                .filter(account -> !account.getLogin().equals(currentLogin))
                .map(AccountMapper::toDto)
                .toList();
    }

    @Override
    public AccountDto updateCurrentAccount(UpdateAccountDto updateAccountDto) {

        if ((ChronoUnit.YEARS.between(updateAccountDto.getBirthday(), LocalDate.now()) < 18)) {
            throw new YoungUserException("Возраст юзера не соответствует требованиям банка");
        }

        Account account = accountRepository.findByLogin(currentUser()).orElseGet(() ->
                Account.builder()
                        .name("Client" + (Math.random() * 900000) + 100000)
                        .login(currentUser())
                        .balance(0L)
                        .birthday(LocalDate.of(2000, 12, 1))
                        .build());

        account.setName(updateAccountDto.getName());
        account.setBirthday(updateAccountDto.getBirthday());

        accountRepository.save(account);

        return AccountMapper.toDto(account);
    }

    @Override
    public AccountDto getCurrentAccount() {
        Account account = accountRepository.findByLogin(currentUser())
                .orElseGet(() ->
                        accountRepository.save(Account.builder()
                                .name("Client" + (Math.random() * 900000) + 100000)
                                .login(currentUser())
                                .balance(0L)
                                .birthday(LocalDate.of(2000, 12, 1))
                                .build()));

        return AccountMapper.toDto(account);
    }

    @Override
    public void deposit(AccountBalanceOperationRequest accountBalanceOperationRequest) {

        Account account = accountRepository.findByLogin(accountBalanceOperationRequest.getLogin()).orElseThrow(
                () -> new NotFoundException("Аккаунт не найден")
        );

        account.setBalance(account.getBalance() + accountBalanceOperationRequest.getAmount());
        accountRepository.save(account);

    }

    @Override
    public void withdraw(AccountBalanceOperationRequest accountBalanceOperationRequest) {
        Account account = accountRepository.findByLogin(accountBalanceOperationRequest.getLogin()).orElseThrow(
                () -> new NotFoundException("Аккаунт не найден")
        );
        if(account.getBalance() < accountBalanceOperationRequest.getAmount()){
            throw new NotEnoughException("Недостаточно средств на счету");
        }
        account.setBalance(account.getBalance() - accountBalanceOperationRequest.getAmount());
        accountRepository.save(account);

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
