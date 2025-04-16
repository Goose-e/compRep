package com.example.companyReputationManagement.dto.company.delete;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

@Data
public class DeleteCompanyRequestDTO implements Serializable {
    @NotNull
    private String companyName;
}
