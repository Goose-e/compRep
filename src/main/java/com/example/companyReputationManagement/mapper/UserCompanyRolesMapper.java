package com.example.companyReputationManagement.mapper;

import com.example.companyReputationManagement.dto.company.add_user.AddUserResponseDTO;
import com.example.companyReputationManagement.dto.company.change_user_role.ChangeUserRoleResponseDTO;
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

    public UserCompanyRoles changeUserStatus(UserCompanyRoles userCompanyRoles, Long newStatus) {
        userCompanyRoles.setStatus(StatusEnum.fromId(newStatus.intValue()));
        userCompanyRoles.setDeleteDate(LocalDateTime.now());
        return userCompanyRoles;
    }

    public ChangeUserRoleResponseDTO changeUserRole(UserCompanyRoles userCompanyRoles, String username) {
        return new ChangeUserRoleResponseDTO(username, userCompanyRoles.getRole().getRole());
    }

    public UserCompanyRoles addUser(Long userId, Long company) {
        UserCompanyRoles userCompanyRoles = new UserCompanyRoles();
        userCompanyRoles.setCompanyId(company);
        userCompanyRoles.setUserId(userId);
        userCompanyRoles.setRole(RoleEnum.USER);
        return userCompanyRoles;
    }

    public AddUserResponseDTO mapNewUserToAddUserResponse(String username) {
        return new AddUserResponseDTO(username);
    }
}
