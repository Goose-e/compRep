package com.example.companyReputationManagement.dao;

import com.example.companyReputationManagement.models.UserCompanyRoles;
import com.example.companyReputationManagement.models.enums.RoleEnum;
import com.example.companyReputationManagement.repo.UserCompanyRolesRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class UserCompanyRolesDao {
    private final UserCompanyRolesRepo userCompanyRolesRepo;

    public UserCompanyRoles save(UserCompanyRoles userCompanyRoles) {
        return userCompanyRolesRepo.save(userCompanyRoles);
    }

    public UserCompanyRoles findByUserId(Long userId, Long companyId) {
        return userCompanyRolesRepo.findByUserIdAndCompanyId(userId, companyId);
    }
    public UserCompanyRoles findByUserCode(String userCode, Long companyId) {
        return userCompanyRolesRepo.findByUserCodeAndCompanyId(userCode, companyId);
    }

    public RoleEnum findRoleByUserCode(String userCode, Long companyId) {
        return userCompanyRolesRepo.findUserCompanyRoles(userCode, companyId);
    }
    public boolean userExistInCompanyByUserCoe(String userCode, Long companyId) {
        return userCompanyRolesRepo.findByUserCodeAndCompanyId(userCode, companyId) != null;
    }
}
