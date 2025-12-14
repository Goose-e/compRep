package com.example.companyReputationManagement.dto.user.ban;

import com.example.companyReputationManagement.httpResponse.HttpResponseBody;

import static com.example.companyReputationManagement.constants.SysConst.EMPTY_STRING;

public class BanUserResponse extends HttpResponseBody<BanUserResponseDTO> {
    private final String httpRequestId = EMPTY_STRING;

    public BanUserResponse() {
        super(EMPTY_STRING);
    }
}
