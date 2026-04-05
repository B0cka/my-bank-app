package com.B0cka.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class NotificationRequestDto {

    @NotBlank
    private String login;

    @NotBlank
    private String message;
}