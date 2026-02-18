package com.example.companyReputationManagement.dto.lab;

import com.example.companyReputationManagement.httpResponse.Dto;
import com.example.companyReputationManagement.httpResponse.ResponseDto;

public record ResponseTestDTO(
        Float volume
) implements Dto, ResponseDto {
}
