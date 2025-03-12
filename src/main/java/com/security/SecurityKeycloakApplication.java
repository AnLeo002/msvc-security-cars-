package com.security;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class SecurityKeycloakApplication {

	public static void main(String[] args) {
		SpringApplication.run(SecurityKeycloakApplication.class, args);
	}

}
