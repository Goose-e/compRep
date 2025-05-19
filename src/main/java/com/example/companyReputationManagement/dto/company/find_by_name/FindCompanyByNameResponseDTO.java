package com.example.companyReputationManagement.dto.company.find_by_name;

import com.example.companyReputationManagement.httpResponse.ResponseDto;
import com.example.companyReputationManagement.models.enums.StatusEnum;
import lombok.Data;

@Data
public class FindCompanyByNameResponseDTO implements ResponseDto {
    private String companyName;
    private String companyCode;
    private String industry;
    private String website;
    private String otzovikUrl;
    private String companyStatus;

    public FindCompanyByNameResponseDTO(String companyName, String companyCode, String industry, String website, String otzovikUrl, StatusEnum companyStatus) {
        this.companyName = companyName;
        this.companyCode = companyCode;
        this.industry = industry;
        this.website = website;
        this.otzovikUrl = otzovikUrl != null ? otzovikUrl : "Company url does not exists or try later";
        this.companyStatus = companyStatus.getStatus();
    }
}
