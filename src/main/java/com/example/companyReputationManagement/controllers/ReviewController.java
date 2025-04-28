package com.example.companyReputationManagement.controllers;

import com.example.companyReputationManagement.dto.review.find.ReviewRequestDto;
import com.example.companyReputationManagement.dto.review.find.ReviewResponse;
import com.example.companyReputationManagement.dto.review.find.ReviewResponseListDto;
import com.example.companyReputationManagement.dto.review.get_all.GetReviewRequestDto;
import com.example.companyReputationManagement.dto.review.get_all.GetReviewResponseListDto;
import com.example.companyReputationManagement.httpResponse.HttpResponseBody;
import com.example.companyReputationManagement.iservice.IReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@CrossOrigin(maxAge = 3600)
@RestController
@RequestMapping("/review")
public class ReviewController {
    private final IReviewService reviewService;

    @GetMapping(value = "/find_reviews")
    public HttpResponseBody<ReviewResponseListDto> findReviews(@Valid @RequestBody ReviewRequestDto reviewRequestDto) {
        HttpResponseBody<ReviewResponseListDto> response = new ReviewResponse();
        try {
            response = reviewService.findReviews(reviewRequestDto);
        }catch (Exception e){
            e.printStackTrace();
        }
       return  response;
    }

    @GetMapping(value = "/get_reviews")
    public HttpResponseBody<GetReviewResponseListDto> getReviews(@Valid @RequestBody GetReviewRequestDto getReviewRequestDto) {
        return reviewService.getReviews(getReviewRequestDto);
    }
}
