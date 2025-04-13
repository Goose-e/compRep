package com.example.companyReputationManagement.repo;

import com.example.companyReputationManagement.models.CompanyUser;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories("com.example.companyReputationManagement.repo")
@EntityScan("com.example.companyReputationManagement.models")
public interface UserRepo extends JpaRepository<CompanyUser, Long> {
    CompanyUser findUserByUsernameOrEmail(String username, String email);

}
