package com.b0cka.controller;

import com.b0cka.controller.dto.AccountDto;
import com.b0cka.controller.dto.CashAction;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class MainController {

    private final RestClient.Builder restClientBuilder;
    private final OAuth2AuthorizedClientService authorizedClientService;

    @Value("${gateway.url}")
    private String gatewayUrl;

    @GetMapping("/")
    public String index() {
        return "redirect:/account";
    }

    @GetMapping("/account")
    public String getAccount(Model model, Authentication authentication) {
        try {
            fillModel(model, authentication, null, null);
        } catch (Exception e) {
            model.addAttribute("errors", List.of("Не удалось загрузить данные аккаунта: " + e.getMessage()));
        }
        return "main";
    }

    @PostMapping("/account")
    public String editAccount(
            Model model,
            Authentication authentication,
            @RequestParam("name") String name,
            @RequestParam("birthdate") LocalDate birthdate
    ) {
        try {
            String token = accessToken(authentication);

            restClientBuilder.build()
                    .post()
                    .uri(gatewayUrl + "/accounts/me")
                    .header("Authorization", "Bearer " + token)
                    .body(Map.of(
                            "name", name,
                            "birthday", birthdate.toString()
                    ))
                    .retrieve()
                    .toBodilessEntity();

            fillModel(model, authentication, null, "Данные аккаунта успешно обновлены");
        } catch (Exception e) {
            try {
                fillModel(model, authentication, List.of("Не удалось обновить аккаунт: " + e.getMessage()), null);
            } catch (Exception ignored) {
                model.addAttribute("errors", List.of("Не удалось обновить аккаунт: " + e.getMessage()));
            }
        }
        return "main";
    }

    @PostMapping("/cash")
    public String editCash(
            Model model,
            Authentication authentication,
            @RequestParam("value") long value,
            @RequestParam("action") String action
    ) {
        try {
            String token = accessToken(authentication);

            restClientBuilder.build()
                    .post()
                    .uri(gatewayUrl + "/cash/actions-with-money")
                    .header("Authorization", "Bearer " + token)
                    .body(Map.of(
                            "cashAction", mapCashAction(action).name(),
                            "sum", value
                    ))
                    .retrieve()
                    .toBodilessEntity();

            String info = mapCashAction(action) == CashAction.DEPOSIT
                    ? "Счёт успешно пополнен"
                    : "Деньги успешно сняты";

            fillModel(model, authentication, null, info);
        } catch (Exception e) {
            try {
                fillModel(model, authentication, List.of("Ошибка операции с наличными: " + e.getMessage()), null);
            } catch (Exception ignored) {
                model.addAttribute("errors", List.of("Ошибка операции с наличными: " + e.getMessage()));
            }
        }
        return "main";
    }

    @PostMapping("/transfer")
    public String transfer(
            Model model,
            Authentication authentication,
            @RequestParam("value") long value,
            @RequestParam("login") String login
    ) {
        try {
            String token = accessToken(authentication);

            restClientBuilder.build()
                    .post()
                    .uri(gatewayUrl + "/transfer/from")
                    .header("Authorization", "Bearer " + token)
                    .body(Map.of(
                            "recipientLogin", login,
                            "amount", value
                    ))
                    .retrieve()
                    .toBodilessEntity();

            fillModel(model, authentication, null, "Перевод успешно выполнен");
        } catch (Exception e) {
            try {
                fillModel(model, authentication, List.of("Ошибка перевода: " + e.getMessage()), null);
            } catch (Exception ignored) {
                model.addAttribute("errors", List.of("Ошибка перевода: " + e.getMessage()));
            }
        }
        return "main";
    }

    private void fillModel(Model model, Authentication authentication, List<String> errors, String info) {
        String token = accessToken(authentication);

        Map<String, Object> account = restClientBuilder.build()
                .get()
                .uri(gatewayUrl + "/accounts/me")
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .body(Map.class);

        List<AccountDto> accounts = restClientBuilder.build()
                .get()
                .uri(gatewayUrl + "/accounts/others")
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .body(List.class);

        model.addAttribute("name", account.get("name"));
        model.addAttribute("birthdate", account.get("birthday"));
        model.addAttribute("sum", account.get("balance"));
        model.addAttribute("accounts", accounts);

        if (errors != null && !errors.isEmpty()) {
            model.addAttribute("errors", errors);
        }
        if (info != null) {
            model.addAttribute("info", info);
        }
    }

    private String accessToken(Authentication authentication) {
        OAuth2AuthorizedClient client = authorizedClientService.loadAuthorizedClient(
                "keycloak",
                authentication.getName()
        );

        if (client == null || client.getAccessToken() == null) {
            throw new IllegalStateException("Не удалось получить access token текущего пользователя");
        }

        return client.getAccessToken().getTokenValue();
    }

    private CashAction mapCashAction(String action) {
        return switch (action) {
            case "PUT", "DEPOSIT" -> CashAction.DEPOSIT;
            case "GET", "WITHDRAW" -> CashAction.WITHDRAW;
            default -> throw new IllegalArgumentException("Неизвестное действие: " + action);
        };
    }
}