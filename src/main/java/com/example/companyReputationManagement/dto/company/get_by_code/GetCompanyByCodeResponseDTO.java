package com.example.companyReputationManagement.dto.company.get_by_code;

import com.example.companyReputationManagement.httpResponse.ResponseDto;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class GetCompanyByCodeResponseDTO implements ResponseDto {
    private String companyName;
    private String companyCode;
    private String industry;
    private String website;
    private String otzovikUrl;

    public GetCompanyByCodeResponseDTO(String companyName, String companyCode, String industry, String website, String otzovikUrl) {
        this.companyName = companyName;
        this.companyCode = companyCode;
        this.industry = industry;
        this.website = website;
        this.otzovikUrl = otzovikUrl != null ? otzovikUrl : "Company url does not exists or try later";
    }
}
