package com.example.companyReputationManagement.dto.review.report;

import com.example.companyReputationManagement.httpResponse.Dto;
import com.example.companyReputationManagement.httpResponse.ResponseDto;

public record GenerateReportResponseDTO(
         byte[] reportImg
) implements Dto, ResponseDto {
}
