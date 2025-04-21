package com.example.companyReputationManagement.mapper;


import com.example.companyReputationManagement.dto.company.create.CompanyCreateRequestDTO;
import com.example.companyReputationManagement.dto.company.create.CompanyCreateResponseDTO;
import com.example.companyReputationManagement.dto.company.get.GetAllCompaniesResponseDTO;
import com.example.companyReputationManagement.iservice.generate.GenerateCode;
import com.example.companyReputationManagement.models.Company;
import com.example.companyReputationManagement.models.enums.StatusEnum;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@AllArgsConstructor
@Component
public class CompanyMapper {
    private final GenerateCode generateCode;

    public Company mapCompanyCreateRequestDTOToCompany(CompanyCreateRequestDTO companyCreateRequestDTO) {
        Company company = new Company();
        company.setName(companyCreateRequestDTO.getCompanyName());
        company.setIndustry(companyCreateRequestDTO.getIndustryName());
        company.setWebsite(companyCreateRequestDTO.getWebsite());
        company.setCompanyCode(generateCode.generateCode(company));
        return company;
    }

    public Company deleteCompany(Company company) {
        company.setStatus(StatusEnum.CLOSED);
        company.setDeleteDate(LocalDateTime.now());
        return company;
    }

    public CompanyCreateResponseDTO mapCompanyToCompanyCreateResponseDTO(Company company) {
        return new CompanyCreateResponseDTO(company.getName(), company.getWebsite(), company.getIndustry(), company.getCompanyCode());
    }

    public GetAllCompaniesResponseDTO mapCompanyToGetAllCompaniesResponseDTO(Company company) {
        return new GetAllCompaniesResponseDTO(
                company.getName(),
                company.getCompanyCode(),
                company.getIndustry(),
                company.getWebsite()
        );
    }
}
