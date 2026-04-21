package com.healthgov.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.healthgov.dto.UserDTO;

@FeignClient(name = "AUTH-SERVICE")
public interface UserClient {

    @GetMapping("/healthGov/getUserById/{userId}")
    UserDTO getUserById(@PathVariable("userId") Long userId);
}