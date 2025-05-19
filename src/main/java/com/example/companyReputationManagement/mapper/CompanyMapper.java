package com.example.companyReputationManagement.mapper;


import com.example.companyReputationManagement.dto.company.create.CompanyCreateRequestDTO;
import com.example.companyReputationManagement.dto.company.create.CompanyCreateResponseDTO;
import com.example.companyReputationManagement.dto.company.edit.EditCompanyRequestDTO;
import com.example.companyReputationManagement.dto.company.edit.EditCompanyResponseDTO;
import com.example.companyReputationManagement.dto.company.find_by_name.FindCompanyByNameResponseDTO;
import com.example.companyReputationManagement.dto.company.find_by_name.FindCompanyByNameResponseListDTO;
import com.example.companyReputationManagement.iservice.generate.GenerateCode;
import com.example.companyReputationManagement.models.Company;
import com.example.companyReputationManagement.models.CompanySourceUrl;
import com.example.companyReputationManagement.models.enums.StatusEnum;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
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

    public FindCompanyByNameResponseListDTO mapCompaniesToFindCompanyByNameResponseListDTO(List<Company> companies, String url) {
        return new FindCompanyByNameResponseListDTO(companies.stream().map(
                company -> new FindCompanyByNameResponseDTO(company.getName(), company.getCompanyCode(), company.getIndustry(), company.getWebsite(), url, company.getStatus())
        ).toList());
    }

    public CompanyCreateResponseDTO mapCompanyCreateRequestDTOToCompanyCreateResponse(Company companyCreateRequestDTO, String url) {
        return new CompanyCreateResponseDTO(companyCreateRequestDTO.getName(), companyCreateRequestDTO.getWebsite(), companyCreateRequestDTO.getIndustry(), "in process...", url);
    }

    public EditCompanyResponseDTO mapCompanyToEditCompanyResponseDTO(Company company, CompanySourceUrl companySourceUrl) {
        return new EditCompanyResponseDTO(
                company.getName(),
                company.getIndustry(),
                company.getWebsite(),
                companySourceUrl.getSourceUrl()
        );
    }

    public Company changeCompanyStatus(Company company, Long newStatus) {
        company.setStatus(StatusEnum.fromId(newStatus.intValue()));
        company.setDeleteDate(newStatus.equals(StatusEnum.CLOSED.getId()) ? LocalDateTime.now() : null);
        return company;
    }


    public Company mapCompanyToEditedCompany(Company company, EditCompanyRequestDTO editCompanyRequestDTO) {
        Optional.ofNullable(editCompanyRequestDTO.getNewCompanyName()).ifPresent(company::setName);
        Optional.ofNullable(editCompanyRequestDTO.getNewCompanyIndustry()).ifPresent(company::setIndustry);
        Optional.ofNullable(editCompanyRequestDTO.getNewCompanyWebsite()).ifPresent(company::setWebsite);
        return company;
    }
}
