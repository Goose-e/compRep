package com.example.companyReputationManagement.dto.company.add_user;

import com.example.companyReputationManagement.httpResponse.HttpResponseBody;

import static com.example.companyReputationManagement.constants.SysConst.EMPTY_STRING;

public class AddUserResponse extends HttpResponseBody<AddUserResponseDTO> {
    private final String httpRequestId = EMPTY_STRING;

    public AddUserResponse() {
        super(EMPTY_STRING);
    }
}
