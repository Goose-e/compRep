package com.example.companyReputationManagement.repo;

import com.example.companyReputationManagement.models.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CompanyRepo extends JpaRepository<Company, Long> {
    boolean existsByName(String name);

    Company findByName(String name);

    Company findByCompanyCode(String companyCode);

    @Query("SELECT c FROM Company c WHERE c.status = 0")
    List<Company> findAllActiveCompany();
}
