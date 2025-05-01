package com.example.companyReputationManagement.dto.user.get_by_code;

import com.example.companyReputationManagement.httpResponse.ResponseDto;

public record GetUserByCodeResponseDTO(
        String username,
        String email,
        String userCode
) implements ResponseDto {
}
