package com.example.companyReputationManagement.dto.company.create;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

@Data
public class CompanyCreateRequestDTO implements Serializable {
    @NotNull
    private String companyName;
    private String industryName;
    private String website;

    public CompanyCreateRequestDTO(String companyName, String industryName, String website) {
        this.companyName = companyName;
        this.industryName = industryName;
        this.website = website;
    }
}
