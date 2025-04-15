package com.example.companyReputationManagement.dto.jwt;

import com.example.companyReputationManagement.httpResponse.HttpResponseBody;

import static com.example.companyReputationManagement.constants.SysConst.EMPTY_STRING;

public class RefreshResponse extends HttpResponseBody<RefreshResponseDTO> {
    private String httpRequestId = EMPTY_STRING;

    public RefreshResponse() {
        super(EMPTY_STRING);
    }

    public String getHttpRequestId() {
        return httpRequestId;
    }
}
