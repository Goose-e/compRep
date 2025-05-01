package com.example.companyReputationManagement.dao;

import com.example.companyReputationManagement.dto.company.get_all.GetAllCompaniesResponseDTO;
import com.example.companyReputationManagement.dto.company.get_all_company_users.GetAllCompanyUsersResponseDTO;
import com.example.companyReputationManagement.dto.company.get_all_user_companies.GetAllUserCompaniesResponseDTO;
import com.example.companyReputationManagement.dto.company.get_by_code.GetCompanyByCodeResponseDTO;
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
    public List<GetAllCompaniesResponseDTO> findAllWithUrls() {
        return companyRepo.findAllActiveCompanyWithUrls();
    }
    public List<Company> findAll() {
        return companyRepo.findAllActiveCompany();
    }

    public List<GetAllUserCompaniesResponseDTO> findAllUsersCompanies(String userCode) {
        return companyRepo.findAllActiveUserCompanies(userCode);
    }

    public GetCompanyByCodeResponseDTO findCompanyByCodeWithUrls(String companyCode,String userCode) {
        return companyRepo.findActiveCompanyByCodeWithUrls(companyCode,userCode);
    }

    public List<GetAllCompanyUsersResponseDTO> findAllCompanyUsers(String userCode) {
        return companyRepo.findAllCompanyUsers(userCode);
    }
}
