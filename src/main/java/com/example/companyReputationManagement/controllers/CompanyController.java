package com.example.companyReputationManagement.controllers;


import com.example.companyReputationManagement.dto.company.create.CompanyCreateRequestDTO;
import com.example.companyReputationManagement.dto.company.create.CompanyCreateResponseDTO;
import com.example.companyReputationManagement.dto.company.delete.DeleteCompanyRequestDTO;
import com.example.companyReputationManagement.dto.company.delete.DeleteCompanyResponseDTO;
import com.example.companyReputationManagement.httpResponse.HttpResponseBody;
import com.example.companyReputationManagement.iservice.ICompanyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@CrossOrigin(maxAge = 3600)
@RestController
@RequestMapping("/company")
public class CompanyController {
    private final ICompanyService companyService;

    @PostMapping(value = "/create")
    public HttpResponseBody<CompanyCreateResponseDTO> createCompany(@Valid @RequestBody CompanyCreateRequestDTO companyCreateRequestDTO) {
        return companyService.createCompany(companyCreateRequestDTO);
    }

    @PostMapping(value = "/delete")
    public HttpResponseBody<DeleteCompanyResponseDTO> deleteCompany(@Valid @RequestBody DeleteCompanyRequestDTO deleteCompanyRequestDTO) {
        return companyService.deleteCompany(deleteCompanyRequestDTO);
    }
}
