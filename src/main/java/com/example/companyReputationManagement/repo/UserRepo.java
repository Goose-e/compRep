package com.example.companyReputationManagement.repo;

import com.example.companyReputationManagement.models.CompanyUser;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

@EnableJpaRepositories("com.example.companyReputationManagement.repo")
@EntityScan("com.example.companyReputationManagement.models")
@Repository
public interface UserRepo extends JpaRepository<CompanyUser, Long> {

    CompanyUser findFirstByUsernameOrEmail(String username, String email);

    CompanyUser findUserByUsername(String username);
    @Query("SELECT c.coreEntityId FROM CompanyUser c WHERE c.username = :username OR c.email = :email")
    Long findCoreEntityIdByUsernameOrEmail(String username, String email);

    CompanyUser findCompanyUserByUserCode(String userCode);


    @Query("SELECT c.coreEntityId FROM CompanyUser c WHERE c.userCode = :userCode")
    Long findCompanyUserIdByUserCode(String userCode);
}
