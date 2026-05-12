package com.b0cka.common.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public class AccountBalanceOperationRequest {

    @NotBlank
    private String login;
    @Min(1)
    private long amount;

    public @NotBlank String getLogin() {
        return login;
    }

    public void setLogin(@NotBlank String login) {
        this.login = login;
    }

    public @Min(1) Long getAmount() {
        return amount;
    }

    public void setAmount(@Min(1) Long amount) {
        this.amount = amount;
    }

    public AccountBalanceOperationRequest(String login, Long amount) {
        this.login = login;
        this.amount = amount;
    }

}

