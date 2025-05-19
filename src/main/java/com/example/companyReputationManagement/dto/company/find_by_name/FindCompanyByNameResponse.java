package com.example.companyReputationManagement.dto.company.find_by_name;

import com.example.companyReputationManagement.httpResponse.HttpResponseBody;

import static com.example.companyReputationManagement.constants.SysConst.EMPTY_STRING;

public class FindCompanyByNameResponse extends HttpResponseBody<FindCompanyByNameResponseListDTO> {
    public FindCompanyByNameResponse() {
        super(EMPTY_STRING);
    }
}
