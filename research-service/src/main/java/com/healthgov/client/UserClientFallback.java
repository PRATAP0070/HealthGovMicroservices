package com.healthgov.client;


import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Component;

import com.healthgov.dto.UserReqDTO;
import com.healthgov.enums.Role;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class UserClientFallback implements UserClient {

    @Override
    public UserReqDTO getUserById(Long userId) {
        log.error("Auth service unavailable. Cannot fetch user {}", userId);
        throw new RuntimeException("Auth service is currently unavailable");
    }

    @Override
    public List<UserReqDTO> getUserByRole(Role role) {
        log.warn("Auth service unavailable. Returning empty user list");
        return Collections.emptyList();
    }
}