package com.example.companyReputationManagement.dto.company.change_user_company_status;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class ChangeCompanyUserStatusRequestDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    @NotNull
    private String companyName;
    @NotNull
    private String username;
    @NotNull
    @Min(0)
    @Max(1)
    private Long newStatusId;

    public ChangeCompanyUserStatusRequestDTO(String companyName, String username, Long newStatusId) {
        this.companyName = companyName;
        this.username = username;
        this.newStatusId = newStatusId;
    }
}
