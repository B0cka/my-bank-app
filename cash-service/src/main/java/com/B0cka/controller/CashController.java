package com.B0cka.controller;

import com.B0cka.dto.CashRequestDto;
import com.B0cka.service.CashService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/cash")
@Validated
public class CashController {

    private final CashService cashService;

    @PostMapping("/actions-with-money")
    public String actionsWithMoney(@RequestBody @Valid CashRequestDto cashRequestDto){
        return cashService.performCashOperation(cashRequestDto.getCashAction(), cashRequestDto.getAmount());
    }

}
