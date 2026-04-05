package com.b0cka.configuration;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.stereotype.Component;

@Component
public class OAuth2DebugRunner implements CommandLineRunner {

    private final ClientRegistrationRepository clientRegistrationRepository;

    public OAuth2DebugRunner(ClientRegistrationRepository clientRegistrationRepository) {
        this.clientRegistrationRepository = clientRegistrationRepository;
    }

    @Override
    public void run(String... args) {
        ClientRegistration registration = clientRegistrationRepository.findByRegistrationId("keycloak");

        if (registration == null) {
            System.out.println("=== OAUTH2 DEBUG === registration 'keycloak' not found");
            return;
        }

        System.out.println("=== OAUTH2 DEBUG ===");
        System.out.println("registrationId = " + registration.getRegistrationId());
        System.out.println("clientId = " + registration.getClientId());
        System.out.println("clientAuthenticationMethod = " + registration.getClientAuthenticationMethod().getValue());
        System.out.println("authorizationGrantType = " + registration.getAuthorizationGrantType().getValue());
        System.out.println("redirectUri = " + registration.getRedirectUri());
        System.out.println("authorizationUri = " + registration.getProviderDetails().getAuthorizationUri());
        System.out.println("tokenUri = " + registration.getProviderDetails().getTokenUri());
        System.out.println("jwkSetUri = " + registration.getProviderDetails().getJwkSetUri());
        System.out.println("====================");
    }
}