package com.example.companyReputationManagement.dto.company.create;

import com.example.companyReputationManagement.httpResponse.HttpResponseBody;
import lombok.Getter;

import java.io.Serial;

import static com.example.companyReputationManagement.constants.SysConst.EMPTY_STRING;

@Getter
public class CompanyCreateResponse extends HttpResponseBody<CompanyCreateResponseDTO> {
    private final String httpRequestId = EMPTY_STRING;
    @Serial
    private static final long serialVersionUID = 1L;
    public CompanyCreateResponse() {
        super(EMPTY_STRING);
    }


}
