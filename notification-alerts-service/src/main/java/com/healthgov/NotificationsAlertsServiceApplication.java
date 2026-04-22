package com.healthgov;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableFeignClients   // ✅ REQUIRED
@EnableAsync
public class NotificationsAlertsServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(NotificationsAlertsServiceApplication.class, args);
    }
}
