package com.example.companyReputationManagement.dto.company.get_all;

import com.example.companyReputationManagement.httpResponse.HttpResponseBody;
import lombok.Getter;

import static com.example.companyReputationManagement.constants.SysConst.EMPTY_STRING;

@Getter
public class GetAllCompaniesResponse extends HttpResponseBody<AllCompaniesResponseListDTO> {
    private final String httpRequestId = EMPTY_STRING;

    public GetAllCompaniesResponse() {
        super(EMPTY_STRING);
    }


}
