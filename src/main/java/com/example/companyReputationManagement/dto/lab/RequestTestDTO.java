package com.example.companyReputationManagement.dto.lab;

import jakarta.validation.constraints.*;

public record RequestTestDTO(
        @NotNull
        @Min(5)
        @Max(250)
        Float weight,
        @NotNull
        @PositiveOrZero
        Float time
) {

}
