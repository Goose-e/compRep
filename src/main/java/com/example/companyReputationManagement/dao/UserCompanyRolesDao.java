package com.example.companyReputationManagement.dao;

import com.example.companyReputationManagement.models.UserCompanyRoles;
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
}
