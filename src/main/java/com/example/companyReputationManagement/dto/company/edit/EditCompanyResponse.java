package com.example.companyReputationManagement.dto.company.edit;

import com.example.companyReputationManagement.httpResponse.HttpResponseBody;
import lombok.Getter;

import static com.example.companyReputationManagement.constants.SysConst.EMPTY_STRING;

@Getter
public class EditCompanyResponse extends HttpResponseBody<EditCompanyResponseDTO> {
    private String httpRequestId = EMPTY_STRING;

    public EditCompanyResponse() {
        super(EMPTY_STRING);
    }

}
