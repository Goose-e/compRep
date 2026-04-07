package com.example.companyReputationManagement.dto.lab.city;

import com.example.companyReputationManagement.dto.lab.ResponseTestDTO;
import com.example.companyReputationManagement.httpResponse.HttpResponseBody;

import static com.example.companyReputationManagement.constants.SysConst.EMPTY_STRING;

public class ResponseCity extends HttpResponseBody<ResponseCityDTO> {
    private String httpRequestId = EMPTY_STRING;

    public ResponseCity() {
        super(EMPTY_STRING);
    }
}
