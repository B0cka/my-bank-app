package com.b0cka.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CashRequestDto {


    @NotNull
    private CashAction cashAction;

    @NotNull
    @Min(1)
    private long amount;
}
