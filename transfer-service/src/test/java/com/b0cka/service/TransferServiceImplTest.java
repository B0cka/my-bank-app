package com.b0cka.service;

import com.b0cka.clients.AccountsClient;
import com.b0cka.dto.TransferRequest;
import com.b0cka.ex.FundsTransferException;
import com.b0cka.ex.InvalidAmount;
import com.b0cka.kafka.producer.TransferEventProducer;
import com.b0cka.service.impl.TransferServiceImpl;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransferServiceImplTest {

    @Mock
    private AccountsClient accountsClient;

    @Mock
    private TransferEventProducer transferEventProducer;

    @Mock
    private MeterRegistry meterRegistry;

    @Mock
    private Counter counter;

    @InjectMocks
    private TransferServiceImpl transferService;

    @BeforeEach
    void setUp() {
        Jwt mockJwt = mock(Jwt.class);
        when(mockJwt.getClaimAsString("preferred_username")).thenReturn("sender_user");
        Authentication auth = new JwtAuthenticationToken(mockJwt, List.of());
        SecurityContextHolder.getContext().setAuthentication(auth);

        lenient().when(meterRegistry.counter(anyString(), anyString(), anyString(), anyString(), anyString()))
                .thenReturn(counter);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("Успешный перевод: вызовы клиентов и отправка события")
    void transferMoney_success() {
        TransferRequest request = new TransferRequest("recipient_user", 100L);
        doNothing().when(accountsClient).withdraw("sender_user", 100L);
        doNothing().when(accountsClient).deposit("recipient_user", 100L);
        doNothing().when(transferEventProducer).sendTransferEvent(anyString(), anyString(), anyLong());

        String result = transferService.transferMoney(request);

        assertEquals("Успешный перевод", result);
        verify(accountsClient).withdraw("sender_user", 100L);
        verify(accountsClient).deposit("recipient_user", 100L);
        verify(transferEventProducer).sendTransferEvent("sender_user", "recipient_user", 100L);
    }

    @Test
    @DisplayName("Перевод самому себе → FundsTransferException + метрика")
    void transferMoney_toSelf_throwsException() {
        TransferRequest request = new TransferRequest("sender_user", 50L);

        FundsTransferException ex = assertThrows(FundsTransferException.class,
                () -> transferService.transferMoney(request));
        assertEquals("Нельзя переводить самому себе!", ex.getMessage());

        verify(meterRegistry).counter(eq("bank.transfer.failed"), eq("from"), eq("sender_user"), eq("to"), eq("sender_user"));
        verify(counter).increment();
    }

    @Test
    @DisplayName("Неверная сумма (<=0) → InvalidAmount + метрика")
    void transferMoney_invalidAmount_throwsException() {
        TransferRequest request = new TransferRequest("recipient_user", 0L);

        InvalidAmount ex = assertThrows(InvalidAmount.class,
                () -> transferService.transferMoney(request));
        assertEquals("Сумма перевода меньше минимальной!", ex.getMessage());

        verify(meterRegistry).counter(eq("bank.transfer.failed"), anyString(), anyString(), anyString(), anyString());
        verify(counter).increment();
    }

    @Test
    @DisplayName("Ошибка при вызове клиента → FundsTransferException + откат метрики")
    void transferMoney_clientError_throwsException() {

        TransferRequest request = new TransferRequest("recipient_user", 100L);
        doThrow(new RuntimeException("Service unavailable")).when(accountsClient).withdraw(anyString(), anyLong());

        FundsTransferException ex = assertThrows(FundsTransferException.class,
                () -> transferService.transferMoney(request));
        assertTrue(ex.getMessage().contains("Ошибка при выполнении перевода"));

        verify(meterRegistry).counter(eq("bank.transfer.failed"), anyString(), anyString(), anyString(), anyString());
        verify(counter).increment();
    }
}