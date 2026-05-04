package com.example.companyReputationManagement.iservice.services.lab;

import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class WeatherService {
    public float getTemperature() {
        Random rand = new Random();
        return rand.nextFloat(-15, 45);
    }

    public float getTemperatureCity(String city) {
        float temp;
        temp = switch (city) {
            case "Minsk" -> 10f;
            case "Brest" -> 15.2f;
            case "Sun" -> 5506f;
            case "Dubai" -> 40f;
            default -> 0f;
        };


        return temp;
    }

}
