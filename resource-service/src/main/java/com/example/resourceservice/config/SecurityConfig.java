package com.example.resourceservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(authorize -> authorize
                .requestMatchers(HttpMethod.POST, "/resources/upload").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/resources/**").hasRole("ADMIN")
                .requestMatchers("/public-endpoint-path").permitAll()
                .anyRequest().authenticated()
        );
        return http.build();
    }
}