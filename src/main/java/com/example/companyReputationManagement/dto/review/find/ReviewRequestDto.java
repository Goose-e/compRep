package com.example.companyReputationManagement.dto.review.find;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

@Data
public class ReviewRequestDto implements Serializable {

    private String url;
    @NotNull
    private String companyCode;

    public ReviewRequestDto(String url, String companyCode) {
        this.url = url;
        this.companyCode = companyCode;
    }

}
