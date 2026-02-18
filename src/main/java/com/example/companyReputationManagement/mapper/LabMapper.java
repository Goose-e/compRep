package com.example.companyReputationManagement.mapper;

import com.example.companyReputationManagement.dto.lab.ResponseTestDTO;
import com.example.companyReputationManagement.iservice.services.lab.WeatherService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class LabMapper {
    private final WeatherService weatherService;
    public ResponseTestDTO createResponse(float weight, float time) {
        float t = weatherService.getTemperature();
        float normal = t>30? t/2+30:30;
        float volume = normal * weight + time * 500 / 60;
        volume = Math.round(volume * 10f) / 10f;
        return new ResponseTestDTO(volume);
    }
}
