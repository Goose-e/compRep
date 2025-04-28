package com.example.companyReputationManagement.dto.review.generate_chart;

import com.example.companyReputationManagement.httpResponse.ResponseDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GenerateChartResponseDto implements ResponseDto {
    private byte[] image;

    public GenerateChartResponseDto(byte[] image) {
        this.image = image;
    }
}
