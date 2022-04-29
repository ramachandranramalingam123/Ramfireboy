package com.beyontec.mdcp.notification;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.web.bind.annotation.CrossOrigin;
@CrossOrigin(origins = "*", allowCredentials = "true", allowedHeaders = "*")
@SpringBootApplication
@EnableDiscoveryClient
public class NotificationApplication {
	
	public static void main(String[] args) throws Exception {
		SpringApplication.run(NotificationApplication.class, args);
	}
}
