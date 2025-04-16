package com.example.companyReputationManagement.mapper;

import com.example.companyReputationManagement.models.Company;
import com.example.companyReputationManagement.models.CompanyUser;
import com.example.companyReputationManagement.models.UserCompanyRoles;
import com.example.companyReputationManagement.models.enums.RoleEnum;
import com.example.companyReputationManagement.models.enums.StatusEnum;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@AllArgsConstructor
@Component
public class UserCompanyRolesMapper {
    public UserCompanyRoles mapUserAndCompanyToUserCompanyRoles(CompanyUser user, Company company) {
        UserCompanyRoles userCompanyRoles = new UserCompanyRoles();
        userCompanyRoles.setCompanyId(company.getCoreEntityId());
        userCompanyRoles.setUserId(user.getCoreEntityId());
        userCompanyRoles.setRole(RoleEnum.OWNER);
        return userCompanyRoles;
    }
    public UserCompanyRoles deleteOwner(UserCompanyRoles userCompanyRoles) {
        userCompanyRoles.setStatus(StatusEnum.CLOSED);
        userCompanyRoles.setDeleteDate(LocalDateTime.now());
        return userCompanyRoles;
    }
}
