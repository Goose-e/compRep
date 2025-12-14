package com.example.companyReputationManagement.dto.review.keyWord;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;


public record KeyWordRequestDTO(
        @NotNull
        String companyCode,
        @NotNull
        @Min(1)
        @Max(4)
        Long sentId)
        implements Serializable {

}
