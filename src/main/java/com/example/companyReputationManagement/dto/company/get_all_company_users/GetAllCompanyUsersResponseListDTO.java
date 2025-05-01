package com.example.companyReputationManagement.dto.company.get_all_company_users;

import com.example.companyReputationManagement.httpResponse.ResponseDto;

import java.util.List;

public record GetAllCompanyUsersResponseListDTO( List<GetAllCompanyUsersResponseDTO> companyUsers) implements ResponseDto {
}
