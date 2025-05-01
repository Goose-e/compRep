package com.example.companyReputationManagement.dto.company.get_all_company_users;

import com.example.companyReputationManagement.httpResponse.HttpResponseBody;

import static com.example.companyReputationManagement.constants.SysConst.EMPTY_STRING;

public class GetAllCompanyUsersResponse extends HttpResponseBody<GetAllCompanyUsersResponseListDTO> {
    private final String httpRequestId = EMPTY_STRING;

    public GetAllCompanyUsersResponse() {
        super(EMPTY_STRING);
    }
}
