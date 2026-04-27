package com.healthgov.client;

import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.healthgov.dto.UserReqDTO;
import com.healthgov.enums.Role;

@FeignClient(name = "auth-service")
public interface UserClient {

    @GetMapping("/healthGov/getUserById/{userId}")
    UserReqDTO getUserById(@PathVariable("userId") Long userId);

    // Changed return type to List to handle REST array response
    @GetMapping("/healthGov/getUserByRole/{role}")
    List<UserReqDTO> getUserByRole(@PathVariable("role") Role role);
}