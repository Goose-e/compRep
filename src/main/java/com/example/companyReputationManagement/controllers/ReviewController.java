package com.example.companyReputationManagement.controllers;

import com.example.companyReputationManagement.dto.review.find.ReviewRequestDto;
import com.example.companyReputationManagement.dto.review.find.ReviewResponse;
import com.example.companyReputationManagement.dto.review.find.ReviewResponseListDto;
import com.example.companyReputationManagement.httpResponse.HttpResponseBody;
import com.example.companyReputationManagement.iservice.IReviewScraperService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@CrossOrigin(maxAge = 3600)
@RestController
@RequestMapping("/review")
public class ReviewController {
    private final IReviewScraperService reviewScraperService;

    @GetMapping(value = "/get_reviews")
    public HttpResponseBody<ReviewResponseListDto> getReviews(@Valid @RequestBody ReviewRequestDto reviewRequestDto) {
        HttpResponseBody<ReviewResponseListDto> response = new ReviewResponse();
        try {
            response = reviewScraperService.getReviews(reviewRequestDto);
        }catch (Exception e){
            e.printStackTrace();
        }
       return  response;
    }
}
