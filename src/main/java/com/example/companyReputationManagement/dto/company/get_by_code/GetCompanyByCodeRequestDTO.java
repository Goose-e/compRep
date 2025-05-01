package com.example.companyReputationManagement.dto.company.get_by_code;

import jakarta.validation.constraints.NotNull;

public record GetCompanyByCodeRequestDTO(
        @NotNull String companyCode
) {
}
