package com.example.companyReputationManagement.dto.company.create;

import com.example.companyReputationManagement.httpResponse.ResponseDto;
import lombok.Data;

@Data
public class CompanyCreateResponseDTO implements ResponseDto {
    private String companyName;
    private String companyWebSite;
    private String companyIndustry;
    private String companyCode;

    public CompanyCreateResponseDTO(String companyName, String companyWebSite, String companyIndustry, String companyCode) {
        this.companyName = companyName;
        this.companyWebSite = companyWebSite;
        this.companyIndustry = companyIndustry;
        this.companyCode = companyCode;
    }
}
