package com.codeconnect.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * CodeConnect Edge Gateway & Authentication Gatekeeper.
 * Dispatches inbound traffic, manages edge sessions, and proxies domain microservices.
 */
@SpringBootApplication
public class GatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }
}
