package com.example.companyReputationManagement.dto.company.change_user_company_status;

import com.example.companyReputationManagement.httpResponse.ResponseDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangeCompanyUserStatusResponseDTO implements ResponseDto {
    private String username;
    private String status;

    public ChangeCompanyUserStatusResponseDTO(String username, String status) {
        this.username = username;
        this.status = status;
    }
}
