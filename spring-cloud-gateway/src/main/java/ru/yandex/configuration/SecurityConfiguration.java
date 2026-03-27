package ru.yandex.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

import java.util.List;
import java.util.stream.Collectors;

@Configuration
public class SecurityConfiguration {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity security) throws Exception {
        return security

                .authorizeHttpRequests(requests ->
                        requests.requestMatchers("/actuator/health")
                                .permitAll()
                                .anyRequest().authenticated())
                .oauth2ResourceServer(customizer -> customizer
                        .jwt(jwtCustomizer -> {
                            JwtAuthenticationConverter authConverter = new JwtAuthenticationConverter();
                            jwtCustomizer.jwtAuthenticationConverter(authConverter);
                        }))
                .build();
    }

}
