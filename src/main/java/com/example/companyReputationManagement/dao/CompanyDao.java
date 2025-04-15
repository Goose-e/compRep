package com.example.companyReputationManagement.dao;

import com.example.companyReputationManagement.models.Company;
import com.example.companyReputationManagement.repo.CompanyRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class CompanyDao {
    private final CompanyRepo companyRepo;
    public boolean existByCompanyName(String companyName) {
        return companyRepo.existsByName(companyName);
    }
    public Company save(Company company) {
        return companyRepo.save(company);
    }
}
