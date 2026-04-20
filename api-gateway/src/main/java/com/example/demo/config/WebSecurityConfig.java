package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
public class WebSecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(
            ServerHttpSecurity http,
            JwtValidationWebFilter jwtValidationWebFilter
    ) {

        http
            // Disable default auth mechanisms
            .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
            .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
            .csrf(ServerHttpSecurity.CsrfSpec::disable)

            // ✅ THIS IS CRITICAL
            // JWT must run at AUTHENTICATION stage
            .addFilterAt(jwtValidationWebFilter, SecurityWebFiltersOrder.AUTHENTICATION)

            // Authorization
            .authorizeExchange(ex -> ex
                .pathMatchers(
                    "/healthGov/login",
                    "/healthGov/citizenRegister",
                    "/healthGov/forgotPassword"
                ).permitAll()
                .pathMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                // ✅ ROLE check (NOT authority)
                .pathMatchers("/healthGov/citizenRegisterForAdmin").hasRole("ADMIN")

                .anyExchange().authenticated()
            );

        return http.build();
    }
}