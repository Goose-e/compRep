package com.example.companyReputationManagement.dto.review.find;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

@Data
public class ReviewRequestDto implements Serializable {


    @NotNull
    private String companyCode;

    public ReviewRequestDto(String companyCode) {
        this.companyCode = companyCode;
    }

}
