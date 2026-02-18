package com.example.companyReputationManagement.dto.lab;

import jakarta.validation.constraints.*;

public record RequestTestDTO(
        Float weight,
        Float time
) {

}
