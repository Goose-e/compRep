package com.example.companyReputationManagement.dto.lab.city;

import com.example.companyReputationManagement.httpResponse.Dto;
import com.example.companyReputationManagement.httpResponse.ResponseDto;

public record ResponseCityDTO(
        Float temp
) implements Dto, ResponseDto {
}
