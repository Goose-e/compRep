package com.example.companyReputationManagement.dto.user.create;

import com.example.companyReputationManagement.httpResponse.HttpResponseBody;

import static com.example.companyReputationManagement.constants.SysConst.EMPTY_STRING;

public class UserCreateResponse extends HttpResponseBody<UserCreateResponseDTO> {
    private final String httpRequestId = EMPTY_STRING;

    public UserCreateResponse() {
        super(EMPTY_STRING);
    }


    public String getHttpRequestId() {
        return httpRequestId;
    }
}
