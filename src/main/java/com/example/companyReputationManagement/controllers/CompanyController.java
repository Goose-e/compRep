package com.example.companyReputationManagement.controllers;


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
import com.example.companyReputationManagement.iservice.ICompanyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
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

    @PostMapping(value = "/find_by_name")
    public HttpResponseBody<FindCompanyByNameResponseListDTO> findCompanyByName(@Valid @RequestBody FindCompanyByNameRequestDTO findCompanyByNameRequestDTO) {
        return companyService.findCompaniesByName(findCompanyByNameRequestDTO);
    }

    @PostMapping(value = "/get_by_code")
    public HttpResponseBody<GetCompanyByCodeResponseDTO> getCompanyByCode(@Valid @RequestBody GetCompanyByCodeRequestDTO companyByCodeRequestDTO) {
        return companyService.getCompanyByCode(companyByCodeRequestDTO);
    }

    @PostMapping(value = "/get_all_staff")
    public HttpResponseBody<GetAllCompanyUsersResponseListDTO> getAllCompanyUsers(@Valid @RequestBody GetAllCompanyUsersRequestDTO getAllCompanyUsersRequestDTO) {
        return companyService.getAllCompanyUsers(getAllCompanyUsersRequestDTO);
    }

    @PostMapping(value = "/change_status")
    public HttpResponseBody<ChangeCompanyStatusResponseDTO> changeStatus(@Valid @RequestBody ChangeCompanyStatusRequestDTO changeCompanyStatusRequestDTO) {
        return companyService.changeCompanyStatus(changeCompanyStatusRequestDTO);
    }

    @GetMapping(value = "/get_all")
    public HttpResponseBody<AllCompaniesResponseListDTO> getAllCompanies() {
        return companyService.getAllCompanies();
    }

    @GetMapping(value = "/get_all_for_user")
    public HttpResponseBody<AllUserCompaniesResponseListDTO> getAllUserCompanies() {
        return companyService.getAllUserCompanies();
    }

    @PatchMapping(value = "/edit")
    public HttpResponseBody<EditCompanyResponseDTO> editCompany(@Valid @RequestBody EditCompanyRequestDTO editCompanyRequestCompany) {
        return companyService.editCompany(editCompanyRequestCompany);
    }

    @PostMapping(value = "/change_role")
    public HttpResponseBody<ChangeUserRoleResponseDTO> changeUserRole(@Valid @RequestBody ChangeUserCompanyRoleRequestDTO changeUserCompanyRoleRequestDTO) {
        return companyService.changeUserRole(changeUserCompanyRoleRequestDTO);
    }

    @PostMapping(value = "/add_user")
    public HttpResponseBody<AddUserResponseDTO> addUserToCompany(@Valid @RequestBody AddUserRequestDTO addUserRequestDTO) {
        return companyService.addUser(addUserRequestDTO);
    }

    @PostMapping(value = "/change_user_status")
    public HttpResponseBody<ChangeCompanyUserStatusResponseDTO> changeUserStatus(@Valid @RequestBody ChangeCompanyUserStatusRequestDTO changeCompanyUserStatusRequestDTO) {
        return companyService.changeUserStatus(changeCompanyUserStatusRequestDTO);
    }

}
