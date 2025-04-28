package com.example.companyReputationManagement.dto.review.get_all;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

@Data
public class GetReviewRequestDto implements Serializable {

    @NotNull
    private String companyCode;

    public GetReviewRequestDto(String companyCode) {
        this.companyCode = companyCode;
    }

}
