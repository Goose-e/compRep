package com.example.companyReputationManagement.dto.user.find;

import com.example.companyReputationManagement.httpResponse.HttpResponseBody;

import static com.example.companyReputationManagement.constants.SysConst.EMPTY_STRING;

public class FindByNameResponse extends HttpResponseBody<FindByNameResponseListDTO> {
    public FindByNameResponse() {
        super(EMPTY_STRING);
    }
}
