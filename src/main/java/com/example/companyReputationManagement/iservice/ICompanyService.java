package com.example.companyReputationManagement.iservice;


import com.example.companyReputationManagement.dto.company.add_user.AddUserRequestDTO;
import com.example.companyReputationManagement.dto.company.add_user.AddUserResponseDTO;
import com.example.companyReputationManagement.dto.company.change_status.ChangeCompanyStatusRequestDTO;
import com.example.companyReputationManagement.dto.company.change_status.ChangeCompanyStatusResponseDTO;
import com.example.companyReputationManagement.dto.company.change_user_company_status.ChangeCompanyUserStatusRequestDTO;
import com.example.companyReputationManagement.dto.company.change_user_company_status.ChangeCompanyUserStatusResponseDTO;
import com.example.companyReputationManagement.dto.company.change_user_role.ChangeUserCompanyRoleRequestDTO;
import com.example.companyReputationManagement.dto.company.change_user_role.ChangeUserRoleResponseDTO;
import com.example.companyReputationManagement.dto.company.create.CompanyCreateRequestDTO;
import com.example.companyReputationManagement.dto.company.create.CompanyCreateResponseDTO;
import com.example.companyReputationManagement.dto.company.edit.EditCompanyRequestDTO;
import com.example.companyReputationManagement.dto.company.edit.EditCompanyResponseDTO;
import com.example.companyReputationManagement.dto.company.find_by_name.FindCompanyByNameRequestDTO;
import com.example.companyReputationManagement.dto.company.find_by_name.FindCompanyByNameResponseListDTO;
import com.example.companyReputationManagement.dto.company.get_all.AllCompaniesResponseListDTO;
import com.example.companyReputationManagement.dto.company.get_all_company_users.GetAllCompanyUsersRequestDTO;
import com.example.companyReputationManagement.dto.company.get_all_company_users.GetAllCompanyUsersResponseListDTO;
import com.example.companyReputationManagement.dto.company.get_all_user_companies.AllUserCompaniesResponseListDTO;
import com.example.companyReputationManagement.dto.company.get_by_code.GetCompanyByCodeRequestDTO;
import com.example.companyReputationManagement.dto.company.get_by_code.GetCompanyByCodeResponseDTO;
import com.example.companyReputationManagement.httpResponse.HttpResponseBody;

public interface ICompanyService {
    HttpResponseBody<CompanyCreateResponseDTO> createCompany(CompanyCreateRequestDTO companyCreateRequestDTO);

    HttpResponseBody<ChangeCompanyStatusResponseDTO> changeCompanyStatus(ChangeCompanyStatusRequestDTO changeCompanyStatusRequestDTO);

    HttpResponseBody<AllCompaniesResponseListDTO> getAllCompanies();

    HttpResponseBody<EditCompanyResponseDTO> editCompany(EditCompanyRequestDTO editCompanyRequestDTO);

    HttpResponseBody<ChangeUserRoleResponseDTO> changeUserRole(ChangeUserCompanyRoleRequestDTO changeUserCompanyRoleRequestDTO);

    HttpResponseBody<AddUserResponseDTO> addUser(AddUserRequestDTO addUserRequestDTO);

    HttpResponseBody<ChangeCompanyUserStatusResponseDTO> changeUserStatus(ChangeCompanyUserStatusRequestDTO changeCompanyUserStatusRequestDTO);

    HttpResponseBody<GetCompanyByCodeResponseDTO> getCompanyByCode(GetCompanyByCodeRequestDTO getCompanyByCodeRequestDTO);

    HttpResponseBody<AllUserCompaniesResponseListDTO> getAllUserCompanies();

    HttpResponseBody<GetAllCompanyUsersResponseListDTO> getAllCompanyUsers(GetAllCompanyUsersRequestDTO getAllCompanyUsersRequestDTO);

    HttpResponseBody<FindCompanyByNameResponseListDTO> findCompaniesByName(FindCompanyByNameRequestDTO findCompanyByNameRequestDTO);
}
