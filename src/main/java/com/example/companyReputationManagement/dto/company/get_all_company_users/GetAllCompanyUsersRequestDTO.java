package com.example.companyReputationManagement.dto.company.get_all_company_users;

import jakarta.validation.constraints.NotNull;

import java.io.Serializable;

public record GetAllCompanyUsersRequestDTO(
        @NotNull
        String companyCode
) implements Serializable {
}
