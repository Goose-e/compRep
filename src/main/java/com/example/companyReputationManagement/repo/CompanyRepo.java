package com.example.companyReputationManagement.repo;

import com.example.companyReputationManagement.models.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyRepo extends JpaRepository<Company, Long> {
    boolean existsByName(String name);

    Company findByName(String name);
}
