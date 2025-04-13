package com.example.companyReputationManagement.dto;

import com.example.companyReputationManagement.httpResponse.HttpResponseBody;

import static com.example.companyReputationManagement.constants.SysConst.EMPTY_STRING;

public class StringResponse extends HttpResponseBody<StringResponseDto> {

    private final String httpRequestId = EMPTY_STRING;

    public StringResponse() {
        super(EMPTY_STRING);
    }


    public String getHttpRequestId() {
        return httpRequestId;
    }
}
