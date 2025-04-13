package com.example.companyReputationManagement.iservice.generate;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class GenerateCode {
    public String generateCode(Object o) {
        return o.getClass().getSimpleName() + "-" + UUID.randomUUID().toString();
    }
}
