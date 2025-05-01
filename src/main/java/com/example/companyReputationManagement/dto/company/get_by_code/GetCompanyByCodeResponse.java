package com.example.companyReputationManagement.dto.company.get_by_code;

import com.example.companyReputationManagement.dto.company.get_all.AllCompaniesResponseListDTO;
import com.example.companyReputationManagement.httpResponse.HttpResponseBody;
import lombok.Getter;

import static com.example.companyReputationManagement.constants.SysConst.EMPTY_STRING;

@Getter
public class GetCompanyByCodeResponse extends HttpResponseBody<GetCompanyByCodeResponseDTO> {
    private final String httpRequestId = EMPTY_STRING;

    public GetCompanyByCodeResponse() {
        super(EMPTY_STRING);
    }


}
