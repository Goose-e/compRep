package com.example.companyReputationManagement.dto.lab;

import com.example.companyReputationManagement.httpResponse.HttpResponseBody;

import static com.example.companyReputationManagement.constants.SysConst.EMPTY_STRING;

public class ResponseTest extends HttpResponseBody<ResponseTestDTO> {
    private String httpRequestId = EMPTY_STRING;

    public ResponseTest() {
        super(EMPTY_STRING);
    }
}
