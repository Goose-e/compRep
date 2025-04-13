package com.example.companyReputationManagement.dto;

import com.example.companyReputationManagement.httpResponse.HttpResponseBody;
import com.example.companyReputationManagement.httpResponse.ResponseDto;

import static com.example.companyReputationManagement.constants.SysConst.EMPTY_STRING;

public class StringResponseDto implements ResponseDto {
    private String data;

    public StringResponseDto(String data) {
        this.data = data;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

}

