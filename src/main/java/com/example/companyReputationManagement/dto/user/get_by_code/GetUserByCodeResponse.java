package com.example.companyReputationManagement.dto.user.get_by_code;

import com.example.companyReputationManagement.httpResponse.HttpResponseBody;

import static com.example.companyReputationManagement.constants.SysConst.EMPTY_STRING;

public class GetUserByCodeResponse extends HttpResponseBody<GetUserByCodeResponseDTO> {
    private final String httpRequestId = EMPTY_STRING;

    public GetUserByCodeResponse() {
        super(EMPTY_STRING);
    }
}
