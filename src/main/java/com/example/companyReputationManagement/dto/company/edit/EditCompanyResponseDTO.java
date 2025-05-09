package com.example.companyReputationManagement.dto.company.edit;

import com.example.companyReputationManagement.httpResponse.ResponseDto;
import lombok.Data;

@Data
public class EditCompanyResponseDTO implements ResponseDto {
    private String newCompanyName;
    private String newCompanyWebsite;
    private String newCompanyIndustry;
    private String newOtzovikUrl;

    public EditCompanyResponseDTO(String newCompanyName, String newCompanyWebsite, String newCompanyIndustry, String newOtzovikUrl) {
        this.newCompanyName = newCompanyName;
        this.newCompanyWebsite = newCompanyWebsite;
        this.newCompanyIndustry = newCompanyIndustry;
        this.newOtzovikUrl = newOtzovikUrl;
    }
}
