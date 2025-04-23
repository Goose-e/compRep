package com.example.companyReputationManagement.dto.review.find;

import com.example.companyReputationManagement.httpResponse.ResponseDto;
import lombok.Data;

import java.util.List;

@Data
public class ReviewResponseListDto implements ResponseDto {
    private List<ReviewResponseDto> reviewList ;

    public ReviewResponseListDto(List<ReviewResponseDto> reviewList) {
        this.reviewList = reviewList;
    }
}
