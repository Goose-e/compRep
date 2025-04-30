package com.example.companyReputationManagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication(scanBasePackages = "com.example.companyReputationManagement")
public class CompanyReputationManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(CompanyReputationManagementApplication.class, args);
    }

}
