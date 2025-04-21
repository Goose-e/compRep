package com.example.companyReputationManagement.dao;

import com.example.companyReputationManagement.models.Company;
import com.example.companyReputationManagement.repo.CompanyRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
public class CompanyDao {
    private final CompanyRepo companyRepo;
    public boolean existByCompanyName(String companyName) {
        return companyRepo.existsByName(companyName);
    }
    public Company findByCompanyCode(String companyCode) {
        return companyRepo.findByCompanyCode(companyCode);
    }
    public Company save(Company company) {
        return companyRepo.save(company);
    }

    public Company findByCompanyName(String companyName) {
        return companyRepo.findByName(companyName);
    }
    public List<Company> findAll() {
        return companyRepo.findAll();
    }
}
