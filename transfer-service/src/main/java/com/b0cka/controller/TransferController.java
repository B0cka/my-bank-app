package com.b0cka.controller;

import com.b0cka.dto.TransferRequest;
import com.b0cka.service.TransferService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/transfer")
@RequiredArgsConstructor
public class TransferController {

    private final TransferService transferService;

    @PostMapping("/from")
    public String transferMoney(@RequestBody @Valid TransferRequest transferRequest){
        return transferService.transferMoney(transferRequest);
    }

}
