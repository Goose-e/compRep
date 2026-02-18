package com.example.companyReputationManagement.mapper;

import com.example.companyReputationManagement.dto.lab.ResponseTestDTO;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class LabMapper {
    public ResponseTestDTO createResponse(float weight, float time) {
        float volume = 30 * weight + time * 500 / 60;
        return new ResponseTestDTO(volume);
    }
}
