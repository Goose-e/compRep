package com.example.companyReputationManagement.dto.review.get_all;

import com.example.companyReputationManagement.httpResponse.ResponseDto;
import lombok.Data;

import java.util.List;

@Data
public class GetReviewResponseListDto implements ResponseDto {
    private List<GetReviewResponseDto> reviewList ;

    public GetReviewResponseListDto(List<GetReviewResponseDto> reviewList) {
        this.reviewList = reviewList;
    }
}
