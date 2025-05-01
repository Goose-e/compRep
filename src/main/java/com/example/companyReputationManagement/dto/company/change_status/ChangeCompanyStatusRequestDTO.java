package com.example.companyReputationManagement.dto.company.change_status;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class ChangeCompanyStatusRequestDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    @NotNull
    private String companyCode;
    @NotNull
    @Min(0)
    @Max(1)
    private Long newStatusId;

    public ChangeCompanyStatusRequestDTO(String companyCode, Long newStatusId) {
        this.companyCode = companyCode;
        this.newStatusId = newStatusId;
    }
}
