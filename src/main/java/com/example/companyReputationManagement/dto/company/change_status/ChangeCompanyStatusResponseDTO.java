package com.example.companyReputationManagement.dto.company.change_status;

import com.example.companyReputationManagement.httpResponse.ResponseDto;
import lombok.Data;

@Data
public class ChangeCompanyStatusResponseDTO implements ResponseDto {
    String companyName;
    String newStatus;
    public ChangeCompanyStatusResponseDTO(String companyName, String newStatus) {
        this.companyName = companyName;
        this.newStatus = newStatus;
    }
}
