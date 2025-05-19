package com.example.companyReputationManagement.dto.user.find;

import jakarta.validation.constraints.NotNull;

public record FindByNameRequestDTO(
        @NotNull
        String username
) {
}
