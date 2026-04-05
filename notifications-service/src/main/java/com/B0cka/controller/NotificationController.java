package com.B0cka.controller;


import com.B0cka.dto.NotificationRequestDto;
import com.B0cka.service.NotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping
    public void send(@RequestBody @Valid NotificationRequestDto request) {
        notificationService.send(request);
    }
}