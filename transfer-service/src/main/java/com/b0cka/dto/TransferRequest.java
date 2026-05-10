package com.b0cka.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TransferRequest {

    @NotNull
    @NotBlank
    private String recipientLogin;
    @NotNull
    @Min(value = 1, message = "Сумма перевода меньше минимальной!")
    private Long amount;

}
