package com.example.companyReputationManagement.dto.company.get_all_user_companies;

import com.example.companyReputationManagement.httpResponse.HttpResponseBody;
import lombok.Getter;

import static com.example.companyReputationManagement.constants.SysConst.EMPTY_STRING;

@Getter
public class GetAllUserCompaniesResponse extends HttpResponseBody<AllUserCompaniesResponseListDTO> {
    private final String httpRequestId = EMPTY_STRING;

    public GetAllUserCompaniesResponse() {
        super(EMPTY_STRING);
    }


}
