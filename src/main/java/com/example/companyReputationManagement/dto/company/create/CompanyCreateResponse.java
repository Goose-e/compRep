package com.example.companyReputationManagement.dto.company.create;

import com.example.companyReputationManagement.httpResponse.HttpResponseBody;

import static com.example.companyReputationManagement.constants.SysConst.EMPTY_STRING;

public class CompanyCreateResponse extends HttpResponseBody<CompanyCreateResponseDTO> {
    private final String httpRequestId = EMPTY_STRING;

    public CompanyCreateResponse() {
        super(EMPTY_STRING);
    }


    public String getHttpRequestId() {
        return httpRequestId;
    }
}
