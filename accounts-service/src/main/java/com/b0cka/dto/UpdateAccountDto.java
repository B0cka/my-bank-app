package com.b0cka.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateAccountDto {

    @NotBlank
    private String name;
    @NotNull
    private LocalDate birthday;

}
