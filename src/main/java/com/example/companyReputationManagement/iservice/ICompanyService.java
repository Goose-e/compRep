package com.example.companyReputationManagement.iservice;


import com.example.companyReputationManagement.dto.company.add_user.AddUserRequestDTO;
import com.example.companyReputationManagement.dto.company.add_user.AddUserResponseDTO;
import com.example.companyReputationManagement.dto.company.change_user_role.ChangeUserCompanyRoleRequestDTO;
import com.example.companyReputationManagement.dto.company.change_user_role.ChangeUserRoleResponseDTO;
import com.example.companyReputationManagement.dto.company.create.CompanyCreateRequestDTO;
import com.example.companyReputationManagement.dto.company.create.CompanyCreateResponseDTO;
import com.example.companyReputationManagement.dto.company.delete.ChangeCompanyStatusRequestDTO;
import com.example.companyReputationManagement.dto.company.delete.ChangeCompanyStatusResponseDTO;
import com.example.companyReputationManagement.dto.company.edit.EditCompanyRequestDTO;
import com.example.companyReputationManagement.dto.company.edit.EditCompanyResponseDTO;
import com.example.companyReputationManagement.dto.company.get.AllCompaniesResponseDTO;
import com.example.companyReputationManagement.httpResponse.HttpResponseBody;

public interface ICompanyService {
    HttpResponseBody<CompanyCreateResponseDTO> createCompany(CompanyCreateRequestDTO companyCreateRequestDTO);

    HttpResponseBody<ChangeCompanyStatusResponseDTO> changeCompanyStatus(ChangeCompanyStatusRequestDTO changeCompanyStatusRequestDTO);

    HttpResponseBody<AllCompaniesResponseDTO> getAllCompanies();

    HttpResponseBody<EditCompanyResponseDTO> editCompany(EditCompanyRequestDTO editCompanyRequestDTO);

    HttpResponseBody<ChangeUserRoleResponseDTO> changeUserRole(ChangeUserCompanyRoleRequestDTO changeUserCompanyRoleRequestDTO);

    HttpResponseBody<AddUserResponseDTO> addUser(AddUserRequestDTO addUserRequestDTO);
}
