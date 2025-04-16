package com.example.companyReputationManagement.dto.company.delete;

import com.example.companyReputationManagement.httpResponse.ResponseDto;
import lombok.Data;

import java.io.Serializable;

@Data
public class DeleteCompanyResponseDTO implements ResponseDto {
    String companyName;

    public DeleteCompanyResponseDTO(String companyName) {
        this.companyName = companyName;
    }
}
