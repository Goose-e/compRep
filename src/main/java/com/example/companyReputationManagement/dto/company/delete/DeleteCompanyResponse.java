package com.example.companyReputationManagement.dto.company.delete;

import com.example.companyReputationManagement.httpResponse.HttpResponseBody;

import static com.example.companyReputationManagement.constants.SysConst.EMPTY_STRING;

public class DeleteCompanyResponse extends HttpResponseBody<DeleteCompanyResponseDTO> {
    private String httpRequestId = EMPTY_STRING;

    public DeleteCompanyResponse() {
        super(EMPTY_STRING);
    }

    public String getHttpRequestId() {
        return httpRequestId;
    }
}
