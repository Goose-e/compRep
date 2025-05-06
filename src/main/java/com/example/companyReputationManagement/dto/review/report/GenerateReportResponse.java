package com.example.companyReputationManagement.dto.review.report;

import com.example.companyReputationManagement.httpResponse.HttpResponseBody;

import static com.example.companyReputationManagement.constants.SysConst.EMPTY_STRING;

public class GenerateReportResponse extends HttpResponseBody<GenerateReportResponseDTO> {
    private String httpRequestId = EMPTY_STRING;

    public GenerateReportResponse() {
        super(EMPTY_STRING);
    }
}
