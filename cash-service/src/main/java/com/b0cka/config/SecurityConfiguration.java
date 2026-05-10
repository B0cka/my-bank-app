package com.b0cka.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfiguration {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity security) throws Exception {
        return security
                .authorizeHttpRequests(requests ->
                        requests
                                .requestMatchers("/actuator/**").permitAll()
                                .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth -> oauth
                        .jwt(jwtCustomizer -> {
                            JwtAuthenticationConverter authConverter = new JwtAuthenticationConverter();
                            jwtCustomizer.jwtAuthenticationConverter(authConverter);
                        }))
                .build();
    }

}
