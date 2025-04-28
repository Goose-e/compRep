package com.example.companyReputationManagement.dto.review.generate_chart;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

@Data
public class GenerateChartRequestDto implements Serializable {
    @NotNull
    private String companyCode;

    public GenerateChartRequestDto(String companyCode) {
        this.companyCode = companyCode;
    }
}
