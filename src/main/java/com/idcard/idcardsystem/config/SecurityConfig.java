package com.idcard.idcardsystem.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable() // disables CSRF protection (useful for API-only apps)
                .authorizeHttpRequests()
                .anyRequest().permitAll(); // allows all endpoints without auth

        return http.build();
    }
}
