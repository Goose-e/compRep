package com.example.companyReputationManagement.dto.company.find_by_name;

import com.example.companyReputationManagement.httpResponse.ResponseDto;

import java.util.List;

public record FindCompanyByNameResponseListDTO(
        List<FindCompanyByNameResponseDTO> companies
) implements ResponseDto {

}
