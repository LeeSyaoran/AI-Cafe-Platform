package com.example.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class CafeServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(CafeServiceApplication.class, args);
    }
}