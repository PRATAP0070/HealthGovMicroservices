package com.healthgov.dto;

import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;

@Component
public class RequestUserContext {

    public Long getUserId(HttpServletRequest request) {
        return Long.valueOf(request.getHeader("X-User-Id"));
    }

    public String getEmail(HttpServletRequest request) {
        return request.getHeader("X-User-Email");
    }

    public String getRole(HttpServletRequest request) {
        return request.getHeader("X-User-Role");
    }
}
