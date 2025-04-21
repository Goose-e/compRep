package com.example.companyReputationManagement.dto.company.edit;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

@Data
public class EditCompanyRequestDTO implements Serializable {
    @NotNull
    private String companyCode;

    private String newCompanyName;
    private String newCompanyWebsite;
    private String newCompanyIndustry;

    public EditCompanyRequestDTO(String companyCode, String newCompanyName, String newCompanyWebsite, String newCompanyIndustry) {
        this.companyCode = companyCode;
        this.newCompanyName = newCompanyName;
        this.newCompanyWebsite = newCompanyWebsite;
        this.newCompanyIndustry = newCompanyIndustry;
    }
}
