package com.example.companyReputationManagement.dto.user.login;

import com.example.companyReputationManagement.httpResponse.HttpResponseBody;

import static com.example.companyReputationManagement.constants.SysConst.EMPTY_STRING;

public class UserLoginResponse extends HttpResponseBody<UserLoginResponseDTO> {
    private String httpRequestId = EMPTY_STRING;

    public UserLoginResponse() {
        super(EMPTY_STRING);
    }

    public String getHttpRequestId() {
        return httpRequestId;
    }
}
