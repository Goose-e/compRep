package com.example.companyReputationManagement.mapper;


import com.example.companyReputationManagement.dto.company.create.CompanyCreateRequestDTO;
import com.example.companyReputationManagement.dto.company.create.CompanyCreateResponseDTO;
import com.example.companyReputationManagement.dto.company.edit.EditCompanyRequestDTO;
import com.example.companyReputationManagement.dto.company.edit.EditCompanyResponseDTO;
import com.example.companyReputationManagement.dto.company.get.GetAllCompaniesResponseDTO;
import com.example.companyReputationManagement.iservice.generate.GenerateCode;
import com.example.companyReputationManagement.models.Company;
import com.example.companyReputationManagement.models.enums.StatusEnum;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

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

    public EditCompanyResponseDTO mapCompanyToEditCompanyResponseDTO(Company company) {
        return new EditCompanyResponseDTO(
                company.getName(),
                company.getIndustry(),
                company.getWebsite()

        );
    }

    public Company changeCompanyStatus(Company company,Long newStatus) {
        company.setStatus(StatusEnum.fromId(newStatus.intValue()));
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

    public Company mapCompanyToEditedCompany(Company company, EditCompanyRequestDTO editCompanyRequestDTO) {
        Optional.ofNullable(editCompanyRequestDTO.getNewCompanyName()).ifPresent(company::setName);
        Optional.ofNullable(editCompanyRequestDTO.getNewCompanyIndustry()).ifPresent(company::setIndustry);
        Optional.ofNullable(editCompanyRequestDTO.getNewCompanyWebsite()).ifPresent(company::setWebsite);
        return company;
    }
}
