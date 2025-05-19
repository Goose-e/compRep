package com.example.companyReputationManagement.dto.company.find_by_name;

import jakarta.validation.constraints.NotBlank;

import java.io.Serializable;

public record FindCompanyByNameRequestDTO(
        @NotBlank
        String companyName
) implements Serializable {
}
