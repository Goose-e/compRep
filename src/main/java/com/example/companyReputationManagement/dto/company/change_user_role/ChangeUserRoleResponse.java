package com.example.companyReputationManagement.dto.company.change_user_role;


import com.example.companyReputationManagement.httpResponse.HttpResponseBody;
import lombok.Getter;
import lombok.Setter;

import static com.example.companyReputationManagement.constants.SysConst.EMPTY_STRING;

@Getter
@Setter
public class ChangeUserRoleResponse extends HttpResponseBody<ChangeUserRoleResponseDTO> {
    private String httpRequestId = EMPTY_STRING;

    public ChangeUserRoleResponse() {
        super(EMPTY_STRING);
    }
}
