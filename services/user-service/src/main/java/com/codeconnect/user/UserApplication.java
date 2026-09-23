package com.codeconnect.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

/**
 * CodeConnect User Management, Profile, & Governance Service.
 * Follows DDD and Clean Architecture standards.
 */
@SpringBootApplication
@ConfigurationPropertiesScan
public class UserApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserApplication.class, args);
    }
}
