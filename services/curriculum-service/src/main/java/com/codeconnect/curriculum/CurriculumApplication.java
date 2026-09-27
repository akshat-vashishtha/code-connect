package com.codeconnect.curriculum;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class CurriculumApplication {

    public static void main(String[] args) {
        SpringApplication.run(CurriculumApplication.class, args);
    }
}
