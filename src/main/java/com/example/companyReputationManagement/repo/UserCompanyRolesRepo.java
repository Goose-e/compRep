package com.example.companyReputationManagement.repo;

import com.example.companyReputationManagement.models.UserCompanyRoles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserCompanyRolesRepo extends JpaRepository<UserCompanyRoles, Long> {
    UserCompanyRoles save(UserCompanyRoles userCompanyRoles);
    UserCompanyRoles findByUserIdAndCompanyId(Long id, Long companyId);
}
