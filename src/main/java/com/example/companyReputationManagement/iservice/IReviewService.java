package com.example.companyReputationManagement.iservice;

import com.example.companyReputationManagement.dto.review.find.ReviewRequestDto;
import com.example.companyReputationManagement.dto.review.find.ReviewResponseListDto;
import com.example.companyReputationManagement.dto.review.get_all.GetReviewRequestDto;
import com.example.companyReputationManagement.dto.review.get_all.GetReviewResponseListDto;
import com.example.companyReputationManagement.httpResponse.HttpResponseBody;

public interface IReviewService {

    HttpResponseBody<ReviewResponseListDto> findReviews(ReviewRequestDto reviewRequestDto) throws Exception;

    HttpResponseBody<GetReviewResponseListDto> getReviews(GetReviewRequestDto getReviewRequestDto);
}
