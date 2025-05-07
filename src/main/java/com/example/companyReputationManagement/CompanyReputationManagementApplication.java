package com.example.companyReputationManagement;

import org.jetbrains.annotations.NotNull;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@EnableScheduling
@SpringBootApplication(scanBasePackages = "com.example.companyReputationManagement")
public class CompanyReputationManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(CompanyReputationManagementApplication.class, args);
    }


}
