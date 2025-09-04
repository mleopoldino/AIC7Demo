package com.mls.workflow.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Disable CSRF for API endpoints
            .headers(headers -> headers.frameOptions(frameOptions -> frameOptions.sameOrigin())) // Allow H2 console frames
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/api/**").permitAll()
                .requestMatchers("/actuator/**").permitAll()
                .requestMatchers("/swagger-ui/**").permitAll()
                .requestMatchers("/v3/api-docs/**").permitAll()
                .requestMatchers("/h2-console/**").permitAll()
                .requestMatchers("/camunda/**").permitAll()
                .requestMatchers("/engine-rest/**").permitAll()
                .requestMatchers("/app/**").permitAll()
                .requestMatchers("/lib/**").permitAll()
                .requestMatchers("/assets/**").permitAll()
                .requestMatchers("/").permitAll()
                .anyRequest().authenticated()
            );
        
        return http.build();
    }
}