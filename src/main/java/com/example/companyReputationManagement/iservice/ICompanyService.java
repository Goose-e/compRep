package com.example.companyReputationManagement.iservice;


import com.example.companyReputationManagement.dto.company.create.CompanyCreateRequestDTO;
import com.example.companyReputationManagement.dto.company.create.CompanyCreateResponseDTO;
import com.example.companyReputationManagement.httpResponse.HttpResponseBody;

public interface ICompanyService {
    HttpResponseBody<CompanyCreateResponseDTO> createCompany(CompanyCreateRequestDTO comapanyCreateRequestDTO);
}
