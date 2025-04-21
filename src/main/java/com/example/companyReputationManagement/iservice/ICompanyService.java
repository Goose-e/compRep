package com.example.companyReputationManagement.iservice;


import com.example.companyReputationManagement.dto.company.create.CompanyCreateRequestDTO;
import com.example.companyReputationManagement.dto.company.create.CompanyCreateResponseDTO;
import com.example.companyReputationManagement.dto.company.delete.DeleteCompanyRequestDTO;
import com.example.companyReputationManagement.dto.company.delete.DeleteCompanyResponseDTO;
import com.example.companyReputationManagement.dto.company.get.AllCompaniesResponseDTO;
import com.example.companyReputationManagement.httpResponse.HttpResponseBody;

public interface ICompanyService {
    HttpResponseBody<CompanyCreateResponseDTO> createCompany(CompanyCreateRequestDTO companyCreateRequestDTO);
    HttpResponseBody<DeleteCompanyResponseDTO> deleteCompany(DeleteCompanyRequestDTO deleteCompanyRequestDTO);
    HttpResponseBody<AllCompaniesResponseDTO> getAllCompanies();
}
