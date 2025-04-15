package com.example.companyReputationManagement.mapper;


import com.example.companyReputationManagement.dto.company.create.CompanyCreateRequestDTO;
import com.example.companyReputationManagement.dto.company.create.CompanyCreateResponseDTO;
import com.example.companyReputationManagement.iservice.generate.GenerateCode;
import com.example.companyReputationManagement.models.Company;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

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

    public CompanyCreateResponseDTO mapCompanyToCompanyCreateResponseDTO(Company company) {
        return new CompanyCreateResponseDTO(company.getName(), company.getWebsite(), company.getIndustry(), company.getCompanyCode());
    }
}
