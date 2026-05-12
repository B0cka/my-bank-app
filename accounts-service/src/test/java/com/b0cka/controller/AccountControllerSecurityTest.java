package com.b0cka.controller;

import com.b0cka.common.dto.AccountBalanceOperationRequest;
import com.b0cka.service.AccountService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AccountController.class)
@Import(AccountControllerSecurityTest.TestSecurityConfig.class)
class AccountControllerSecurityTest {

    @MockBean
    private AccountService accountService;

    @MockBean
    private JwtDecoder jwtDecoder;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("/accounts/internal/deposit без JWT → 403")
    void deposit_withoutJwt_returnsForbidden() throws Exception {
        mockMvc.perform(post("/accounts/internal/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new AccountBalanceOperationRequest("user", 100L))))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("/accounts/internal/deposit с невалидным JWT → 403")
    void deposit_withInvalidJwt_returnsForbidden() throws Exception {
        when(jwtDecoder.decode("invalid_token_here"))
                .thenThrow(new JwtException("Invalid token"));

        mockMvc.perform(post("/accounts/internal/deposit")
                        .header("Authorization", "Bearer invalid_token_here")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new AccountBalanceOperationRequest("user", 100L))))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("/accounts/internal/withdraw без JWT → 403")
    void withdraw_withoutJwt_returnsForbidden() throws Exception {
        mockMvc.perform(post("/accounts/internal/withdraw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new AccountBalanceOperationRequest("user", 100L))))
                .andExpect(status().isForbidden());
    }

    @TestConfiguration
    static class TestSecurityConfig {
        @Bean
        public SecurityFilterChain testFilterChain(HttpSecurity http) throws Exception {
            http
                    .csrf(csrf -> csrf.disable())
                    .authorizeHttpRequests(auth -> auth
                            .requestMatchers("/accounts/internal/**").authenticated()
                            .anyRequest().permitAll()
                    );
            return http.build();
        }
    }
}