package com.example.companyReputationManagement.iservice;

import com.example.companyReputationManagement.dto.review.find.ReviewRequestDto;
import com.example.companyReputationManagement.dto.review.find.ReviewResponseListDto;
import com.example.companyReputationManagement.httpResponse.HttpResponseBody;

import java.io.IOException;

public interface IReviewScraperService {

    HttpResponseBody<ReviewResponseListDto> getReviews(ReviewRequestDto reviewRequestDto) throws Exception;

}
