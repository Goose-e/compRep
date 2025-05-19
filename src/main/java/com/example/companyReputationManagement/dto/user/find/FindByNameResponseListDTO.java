package com.example.companyReputationManagement.dto.user.find;

import com.example.companyReputationManagement.httpResponse.ResponseDto;

import java.util.List;

public record FindByNameResponseListDTO(
        List<FindByNameResponseDTO> users
) implements ResponseDto {
}
