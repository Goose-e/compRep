package com.example.companyReputationManagement.controllers;

import com.example.companyReputationManagement.dto.review.find.ReviewRequestDto;
import com.example.companyReputationManagement.dto.review.find.ReviewResponse;
import com.example.companyReputationManagement.dto.review.find.ReviewResponseListDto;
import com.example.companyReputationManagement.dto.review.generate_chart.GenerateChartRequestDto;
import com.example.companyReputationManagement.dto.review.generate_chart.GenerateChartResponseDto;
import com.example.companyReputationManagement.dto.review.get_all.GetReviewRequestDto;
import com.example.companyReputationManagement.dto.review.get_all.GetReviewResponseListDto;
import com.example.companyReputationManagement.dto.review.get_all_by_sent.GetAllBySentRequestDTO;
import com.example.companyReputationManagement.dto.review.get_all_by_sent.GetAllBySentResponseListDTO;
import com.example.companyReputationManagement.dto.review.report.GenerateReportRequestDTO;
import com.example.companyReputationManagement.dto.review.report.GenerateReportResponseDTO;
import com.example.companyReputationManagement.httpResponse.HttpResponseBody;
import com.example.companyReputationManagement.iservice.IReviewService;
import com.itextpdf.text.DocumentException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RequiredArgsConstructor
@CrossOrigin(maxAge = 3600)
@RestController
@RequestMapping("/review")
public class ReviewController {
    private final IReviewService reviewService;

    @PostMapping(value = "/find_reviews")
    public HttpResponseBody<ReviewResponseListDto> findReviews(@Valid @RequestBody ReviewRequestDto reviewRequestDto) {
        HttpResponseBody<ReviewResponseListDto> response = new ReviewResponse();
        try {
            response = reviewService.findReviews(reviewRequestDto);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return response;
    }

    @PostMapping(value = "/get_reviews")
    public HttpResponseBody<GetReviewResponseListDto> getReviews(@Valid @RequestBody GetReviewRequestDto getReviewRequestDto) {
        return reviewService.getReviews(getReviewRequestDto);
    }

    @PostMapping(value = "/get_review_chart")
    public HttpResponseBody<GenerateChartResponseDto> getReviewsCharts(@Valid @RequestBody GenerateChartRequestDto generateChartRequestDto) throws IOException {
        return reviewService.generateCharts(generateChartRequestDto);
    }

    @PostMapping(value = "/get_reviews_by_sent")
    public HttpResponseBody<GetAllBySentResponseListDTO> getReviewsBySent(@Valid @RequestBody GetAllBySentRequestDTO getAllBySentRequestDTO) throws IOException {
        return reviewService.getAllReviewsBySentType(getAllBySentRequestDTO);
    }

    @PostMapping(value = "/report")
    public HttpResponseBody<GenerateReportResponseDTO> generateReport(@Valid @RequestBody GenerateReportRequestDTO generateReportRequestDTO) throws DocumentException, IOException {
        return reviewService.generateReport(generateReportRequestDTO);
    }
}
