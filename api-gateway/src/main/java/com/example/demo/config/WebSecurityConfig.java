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
                    "/healthGov/getUserById/**",
                    "/healthGov/citizenRegister",
                    "/healthGov/forgotPassword/**",
                    "/citizen/**"
                ).permitAll()

                .pathMatchers(HttpMethod.OPTIONS, "/**").permitAll()
 
                // ✅ ROLE check (NOT authority)
                .pathMatchers("/healthGov/userRegisterByAdmin").hasRole("ADMIN")
                .pathMatchers("/audit_log/**").hasRole("ADMIN")
                .pathMatchers("/health-profile/**").hasRole("PROVIDER")
                .pathMatchers("/document/**").hasAnyRole("CITIZEN","PROVIDER")
                .pathMatchers("/research/**").hasRole("RESEARCHER")
                .pathMatchers("/manager/**").hasRole("MANAGER")
                .pathMatchers("/api/v1/compliance-records/**").hasAnyRole("COMPLIANCE","AUDITOR")
                .pathMatchers("/api/v1/audits/**").hasRole("AUDITOR")
                .pathMatchers("/api/programs/**").hasRole("MANAGER")
                .pathMatchers("/resources/**").hasRole("MANAGER")
                .pathMatchers("/infrastructures/**").hasRole("MANAGER")
                .pathMatchers("/api/enrollments/**").hasRole("CITIZEN")

 
                .anyExchange().authenticated()

            );
 
        return http.build();

    }

}
 