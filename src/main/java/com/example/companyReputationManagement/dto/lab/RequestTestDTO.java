package com.example.companyReputationManagement.dto.lab;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.PositiveOrZero;

public record RequestTestDTO(
        @NotEmpty
        @Min(5)
        @Max(250)
        Float weight,
        @NotEmpty
        @PositiveOrZero
        Float time
) {

}
