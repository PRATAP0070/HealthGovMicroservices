package com.healthgov.fallbacks;

import org.springframework.stereotype.Service;

import com.healthgov.dtos.UserResponseDto;
import com.healthgov.feignclients.UserClient;

import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserServiceClient {

    private final UserClient userClient;

    public UserServiceClient(UserClient userClient) {
        this.userClient = userClient;
    }

    @CircuitBreaker(name = "authServiceCB", fallbackMethod = "userFallback")
    @Retry(name = "authServiceCB")
	@Bulkhead(name = "authServiceCB")
    public UserResponseDto getUserById(Long userId) {
        return userClient.getUserById(userId);
    }

    public UserResponseDto userFallback(Long userId, Throwable ex) {
    	log.info("User Fall Back");
        UserResponseDto dto = new UserResponseDto();
        dto.setUserId(null);
        dto.setEmail(null);
        dto.setName("Auth Service is Down");
        return dto;
    }
}

