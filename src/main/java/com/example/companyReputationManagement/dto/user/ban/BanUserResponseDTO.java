package com.example.companyReputationManagement.dto.user.ban;

import com.example.companyReputationManagement.dto.company.edit.EditCompanyRequestDTO;
import com.example.companyReputationManagement.httpResponse.ResponseDto;

public record BanUserResponseDTO(
        String username
) implements ResponseDto {
}
