package com.example.companyReputationManagement;

import com.example.companyReputationManagement.dao.CompanyDao;
import com.example.companyReputationManagement.dao.UserCompanyRolesDao;
import com.example.companyReputationManagement.dao.UserDao;
import com.example.companyReputationManagement.dto.company.change_user_company_status.ChangeCompanyUserStatusRequestDTO;
import com.example.companyReputationManagement.dto.company.change_user_company_status.ChangeCompanyUserStatusResponseDTO;
import com.example.companyReputationManagement.dto.company.get_all.AllCompaniesResponseListDTO;
import com.example.companyReputationManagement.dto.company.get_all.GetAllCompaniesResponseDTO;
import com.example.companyReputationManagement.httpResponse.HttpResponseBody;
import com.example.companyReputationManagement.iservice.IJwtService;
import com.example.companyReputationManagement.iservice.services.company.CompanyService;
import com.example.companyReputationManagement.iservice.services.transactions.CompanyTrans;
import com.example.companyReputationManagement.mapper.UserCompanyRolesMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CompanyTest {


    @Mock
    private CompanyDao companyDao;

    @Mock
    private UserDao userDao;
    @Mock
    private IJwtService jwtService;
    @Mock
    private UserCompanyRolesDao userCompanyRolesDao;

    @Mock
    private UserCompanyRolesMapper userCompanyRolesMapper;

    @Mock
    private CompanyTrans companyTrans;
    @InjectMocks
    private CompanyService companyService;

    @Test
    void testChangeUserStatus_CompanyNotFound() {
        // given
        ChangeCompanyUserStatusRequestDTO request = new ChangeCompanyUserStatusRequestDTO();
        request.setCompanyCode("invalid_code");

        when(companyDao.findByCompanyCode(request.getCompanyCode())).thenReturn(null);

        // when
        HttpResponseBody<ChangeCompanyUserStatusResponseDTO> result = companyService.changeUserStatus(request);

        // then
        assertEquals("Company not found", result.getError());
        assertEquals("Company not found", result.getMessage());
    }

    @Test
    void testGetAllCompanies_success() {
        GetAllCompaniesResponseDTO company1 = new GetAllCompaniesResponseDTO
                ("comp", "dasg", null, null, null);
        GetAllCompaniesResponseDTO company2 = new GetAllCompaniesResponseDTO
                ("comp2", "code", null, null, null);

        when(companyDao.findAllWithUrls()).thenReturn(List.of(company1, company2));
        HttpResponseBody<AllCompaniesResponseListDTO> resultSuccess = companyService.getAllCompanies();
        assertEquals("All companies found", resultSuccess.getMessage());

    }

    @Test
    void testGetAllCompanies_CompanyNotFound() {
        when(companyDao.findAllWithUrls()).thenReturn(new ArrayList<>());
        HttpResponseBody<AllCompaniesResponseListDTO> resultBad = companyService.getAllCompanies();
        assertEquals("Companies not found", resultBad.getMessage());
    }
}
