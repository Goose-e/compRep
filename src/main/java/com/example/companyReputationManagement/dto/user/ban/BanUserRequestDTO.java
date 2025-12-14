package com.example.companyReputationManagement.dto.user.ban;

import jakarta.validation.constraints.NotNull;

import java.io.Serializable;

public record BanUserRequestDTO(
        @NotNull
        String userCode
)  implements Serializable {
}
