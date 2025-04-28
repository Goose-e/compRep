package com.example.companyReputationManagement.dto.review.generate_chart;

import com.example.companyReputationManagement.httpResponse.HttpResponseBody;

import static com.example.companyReputationManagement.constants.SysConst.EMPTY_STRING;

public class GenerateChartResponse extends HttpResponseBody<GenerateChartResponseDto> {
    private String httpRequestId = EMPTY_STRING;

    public GenerateChartResponse() {
        super(EMPTY_STRING);
    }
}
