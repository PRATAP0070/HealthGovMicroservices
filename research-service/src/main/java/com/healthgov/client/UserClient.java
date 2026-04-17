package com.healthgov.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.healthgov.dto.UserReqDTO;

@FeignClient(name = "auth-service")
public interface UserClient {

    @GetMapping("/healthGov/getUserById/{userId}")
    UserReqDTO getUserById(@PathVariable("userId") Long userId);
}