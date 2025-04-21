package com.example.companyReputationManagement.dto.company.change_status;

import com.example.companyReputationManagement.httpResponse.HttpResponseBody;
import lombok.Getter;

import java.io.Serial;

import static com.example.companyReputationManagement.constants.SysConst.EMPTY_STRING;

@Getter
public class ChangeCompanyStatus extends HttpResponseBody<ChangeCompanyStatusResponseDTO> {
    @Serial
    private static final long serialVersionUID = 1L;
    private final String httpRequestId = EMPTY_STRING;

    public ChangeCompanyStatus() {
        super(EMPTY_STRING);
    }

}
