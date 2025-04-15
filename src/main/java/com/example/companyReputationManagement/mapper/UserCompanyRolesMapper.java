package com.example.companyReputationManagement.mapper;

import com.example.companyReputationManagement.models.Company;
import com.example.companyReputationManagement.models.CompanyUser;
import com.example.companyReputationManagement.models.UserCompanyRoles;
import com.example.companyReputationManagement.models.enums.RoleEnum;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

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
}
