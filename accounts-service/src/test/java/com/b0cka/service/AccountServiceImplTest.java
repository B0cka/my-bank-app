package com.b0cka.service;

import com.b0cka.common.dto.AccountBalanceOperationRequest;
import com.b0cka.entity.Account;
import com.b0cka.ex.NotEnoughException;
import com.b0cka.ex.NotFoundException;
import com.b0cka.repository.AccountRepository;
import com.b0cka.service.impl.AccountServiceImpl;
import io.micrometer.core.instrument.Counter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import io.micrometer.core.instrument.MeterRegistry;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceImplTest {

    @Mock
    private MeterRegistry meterRegistry;

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountServiceImpl accountService;

    @Test
    @DisplayName("Deposit: успешное пополнение")
    void deposit_success() {
        Account account = Account.builder()
                .login("test_user")
                .balance(100L)
                .build();
        when(accountRepository.findByLogin("test_user")).thenReturn(Optional.of(account));
        when(accountRepository.save(any())).thenReturn(account);

        accountService.deposit(new AccountBalanceOperationRequest("test_user", 50L));

        assertEquals(150L, account.getBalance());
        verify(accountRepository).save(account);
    }

    @Test
    @DisplayName("Deposit: аккаунт не найден → исключение")
    void deposit_accountNotFound_throwsException() {
        when(accountRepository.findByLogin("nonexistent")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () ->
                accountService.deposit(new AccountBalanceOperationRequest("nonexistent", 50L))
        );
    }

    @Test
    @DisplayName("Withdraw: успешное снятие")
    void withdraw_success() {
        Account account = Account.builder()
                .login("test_user")
                .balance(100L)
                .build();
        when(accountRepository.findByLogin("test_user")).thenReturn(Optional.of(account));
        when(accountRepository.save(any())).thenReturn(account);

        accountService.withdraw(new AccountBalanceOperationRequest("test_user", 30L));

        assertEquals(70L, account.getBalance());
        verify(accountRepository).save(account);
    }

    @Test
    @DisplayName("Withdraw: недостаточно средств → исключение")
    void withdraw_insufficientFunds_throwsException() {
        Account account = Account.builder().login("test_user").balance(50L).build();
        when(accountRepository.findByLogin("test_user")).thenReturn(Optional.of(account));

        Counter mockCounter = mock(Counter.class);
        when(meterRegistry.counter(anyString(), anyString(), anyString())).thenReturn(mockCounter);

        assertThrows(NotEnoughException.class, () ->
                accountService.withdraw(new AccountBalanceOperationRequest("test_user", 100L))
        );
        verify(mockCounter).increment();
    }
}