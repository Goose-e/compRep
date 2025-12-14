package com.example.companyReputationManagement.iservice;

import com.example.companyReputationManagement.dto.review.find.ReviewRequestDto;
import com.example.companyReputationManagement.dto.review.find.ReviewResponseListDto;
import com.example.companyReputationManagement.dto.review.generate_chart.GenerateChartRequestDto;
import com.example.companyReputationManagement.dto.review.generate_chart.GenerateChartResponseDto;
import com.example.companyReputationManagement.dto.review.get_all.GetReviewRequestDto;
import com.example.companyReputationManagement.dto.review.get_all.GetReviewResponseListDto;
import com.example.companyReputationManagement.dto.review.get_all_by_sent.GetAllBySentRequestDTO;
import com.example.companyReputationManagement.dto.review.get_all_by_sent.GetAllBySentResponseListDTO;
import com.example.companyReputationManagement.dto.review.keyWord.KeyWordRequestDTO;
import com.example.companyReputationManagement.dto.review.keyWord.KeyWordResponseDTO;
import com.example.companyReputationManagement.dto.review.report.GenerateReportRequestDTO;
import com.example.companyReputationManagement.dto.review.report.GenerateReportResponseDTO;
import com.example.companyReputationManagement.httpResponse.HttpResponseBody;
import com.itextpdf.text.DocumentException;

import java.io.IOException;

public interface IReviewService {

    HttpResponseBody<ReviewResponseListDto> findReviews(ReviewRequestDto reviewRequestDto) throws Exception;

    HttpResponseBody<GetReviewResponseListDto> getReviews(GetReviewRequestDto getReviewRequestDto);

    HttpResponseBody<GenerateChartResponseDto> generateCharts(GenerateChartRequestDto generateChartRequestDto) throws IOException;

    HttpResponseBody<GetAllBySentResponseListDTO> getAllReviewsBySentType(GetAllBySentRequestDTO allBySentRequestDTO);

    HttpResponseBody<GenerateReportResponseDTO> generateReport(GenerateReportRequestDTO generateReportRequestDTo) throws DocumentException, IOException;

    HttpResponseBody<KeyWordResponseDTO> keyWordAnalysis(KeyWordRequestDTO keyWordRequestDTO);
}
