package com.example.companyReputationManagement.dto.company.get_all_company_users;

import com.example.companyReputationManagement.httpResponse.ResponseDto;
import com.example.companyReputationManagement.models.enums.RoleEnum;
import com.example.companyReputationManagement.models.enums.StatusEnum;
import lombok.Data;

@Data
public class GetAllCompanyUsersResponseDTO implements ResponseDto {
    private String username;
    private String userRole;
    private String userStatus;
    private String userCode;

    public GetAllCompanyUsersResponseDTO(String username, String userCode, RoleEnum userRole, StatusEnum userStatus) {
        this.username = username;
        this.userCode = userCode;
        this.userRole = userRole.getRole();
        this.userStatus = userStatus.getStatus();
    }
}
