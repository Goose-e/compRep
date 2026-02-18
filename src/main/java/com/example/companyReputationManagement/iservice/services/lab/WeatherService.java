package com.example.companyReputationManagement.iservice.services.lab;

import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class WeatherService {
    public float getTemperature() {
        Random rand = new Random();
        return rand.nextFloat(-15,45);
    }
}
