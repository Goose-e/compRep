package com.example.companyReputationManagement.dto.review.report;

import jakarta.validation.constraints.NotNull;

import java.io.Serializable;

public record GenerateReportRequestDTO(
        @NotNull
        String companyCode
) implements Serializable {

}
