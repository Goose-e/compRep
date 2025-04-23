package com.example.companyReputationManagement.dto.user.edit;

import com.example.companyReputationManagement.httpResponse.HttpResponseBody;

import static com.example.companyReputationManagement.constants.SysConst.EMPTY_STRING;

public class EditUserResponse extends HttpResponseBody<EditUserResponseDTO> {
    private final String httpRequestId = EMPTY_STRING;

    public EditUserResponse() {
        super(EMPTY_STRING);
    }


    public String getHttpRequestId() {
        return httpRequestId;
    }
}
