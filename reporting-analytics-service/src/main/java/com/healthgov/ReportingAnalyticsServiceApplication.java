package com.healthgov;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class ReportingAnalyticsServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ReportingAnalyticsServiceApplication.class, args);
	}
}