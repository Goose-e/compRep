package com.example.companyReputationManagement.dto.review.get_all_by_sent;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;

import java.io.Serializable;


public record GetAllBySentRequestDTO(
        @NotNull
        String companyCode,
        @NotNull
        @Min(1)
        @Max(4)
        Long sentId)
        implements Serializable {

}
