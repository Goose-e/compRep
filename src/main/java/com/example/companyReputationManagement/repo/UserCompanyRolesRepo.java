package com.example.companyReputationManagement.repo;

import com.example.companyReputationManagement.models.UserCompanyRoles;
import com.example.companyReputationManagement.models.enums.RoleEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserCompanyRolesRepo extends JpaRepository<UserCompanyRoles, Long> {
    UserCompanyRoles save(UserCompanyRoles userCompanyRoles);

    UserCompanyRoles findByUserIdAndCompanyId(Long id, Long companyId);

    @Query("SELECT ur.role from UserCompanyRoles ur JOIN CompanyUser cu ON cu.coreEntityId = ur.userId " +
            "WHERE cu.userCode =:userCode and ur.companyId =:companyId ")
    RoleEnum findUserCompanyRoles(String userCode, Long companyId);

    @Query("SELECT ur from UserCompanyRoles ur JOIN CompanyUser cu ON cu.coreEntityId = ur.userId " +
            "WHERE cu.userCode =:userCode and ur.companyId =:companyId ")
    UserCompanyRoles findByUserCodeAndCompanyId(String userCode, Long companyId);
}
