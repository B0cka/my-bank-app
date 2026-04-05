package com.b0cka.configuration;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class OAuth2SecretDebugRunner implements CommandLineRunner {

    private final Environment environment;

    public OAuth2SecretDebugRunner(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void run(String... args) {
        String secret = environment.getProperty("spring.security.oauth2.client.registration.keycloak.client-secret");
        System.out.println("=== OAUTH2 SECRET DEBUG === " + secret);
    }
}