package com.example.companyReputationManagement.iservice.services.service.comapany;


import com.example.companyReputationManagement.dao.CompanyDao;
import com.example.companyReputationManagement.dao.UserCompanyRolesDao;
import com.example.companyReputationManagement.dao.UserDao;
import com.example.companyReputationManagement.dto.company.create.CompanyCreateRequestDTO;
import com.example.companyReputationManagement.dto.company.create.CompanyCreateResponse;
import com.example.companyReputationManagement.dto.company.create.CompanyCreateResponseDTO;
import com.example.companyReputationManagement.dto.company.delete.DeleteCompanyRequestDTO;
import com.example.companyReputationManagement.dto.company.delete.DeleteCompanyResponse;
import com.example.companyReputationManagement.dto.company.delete.DeleteCompanyResponseDTO;
import com.example.companyReputationManagement.dto.company.get.AllCompaniesResponseDTO;
import com.example.companyReputationManagement.dto.company.get.GetAllCompaniesResponse;
import com.example.companyReputationManagement.httpResponse.HttpResponseBody;
import com.example.companyReputationManagement.iservice.ICompanyService;
import com.example.companyReputationManagement.iservice.services.transactions.CompanyTrans;
import com.example.companyReputationManagement.mapper.CompanyMapper;
import com.example.companyReputationManagement.mapper.UserCompanyRolesMapper;
import com.example.companyReputationManagement.models.Company;
import com.example.companyReputationManagement.models.CompanyUser;
import com.example.companyReputationManagement.models.UserCompanyRoles;
import com.example.companyReputationManagement.models.enums.RoleEnum;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.companyReputationManagement.constants.SysConst.OC_BUGS;
import static com.example.companyReputationManagement.constants.SysConst.OC_OK;

@Service
@RequiredArgsConstructor
public class CompanyService implements ICompanyService {
    private final CompanyDao companyDao;
    private final CompanyMapper companyMapper;
    private final UserDao userDao;
    private final UserCompanyRolesDao userCompanyRolesdao;
    private final UserCompanyRolesMapper userCompanyRolesMapper;
    private final CompanyTrans companyTrans;
    private static final Logger logger = LoggerFactory.getLogger(CompanyService.class);

    @Override
    public HttpResponseBody<CompanyCreateResponseDTO> createCompany(CompanyCreateRequestDTO companyCreateRequestDTO) {
        HttpResponseBody<CompanyCreateResponseDTO> response = new CompanyCreateResponse();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        try {
            Jwt jwt = (Jwt) authentication.getPrincipal();
            String username = jwt.getClaim("username");
            CompanyUser user = userDao.findUserByUserName(username);
            if (user != null) {
                if (!companyDao.existByCompanyName(companyCreateRequestDTO.getCompanyName())) {
                    Company company = companyMapper.mapCompanyCreateRequestDTOToCompany(companyCreateRequestDTO);
                    UserCompanyRoles userCompanyRoles = userCompanyRolesMapper.mapUserAndCompanyToUserCompanyRoles(user, company);
                    try {
                        companyTrans.save(company, userCompanyRoles);
                        response.setMessage("Company created successfully.User role changed successfully.");
                        response.setResponseEntity(companyMapper.mapCompanyToCompanyCreateResponseDTO(company));
                    } catch (DataIntegrityViolationException e) {
                        response.setError("Transaction failed");
                        response.setMessage("Data integrity violation: " + e.getMessage());
                        logger.error("Data integrity error while saving company and user role: ", e);
                    } catch (Exception e) {
                        response.setError("Transaction failed");
                        response.setMessage("Unexpected error: " + e.getMessage());
                        logger.error("Unexpected error while saving company and user role: ", e);
                    }
                } else {
                    response.setMessage("Company name already exist");
                }
            } else {
                response.setError("User not found");
                response.setMessage("User not found");
            }
        } catch (Exception e) {
            response.setError("get user error");
            response.setMessage("get user error");
        }
        if (response.getErrors().isEmpty()) {
            response.setResponseCode(OC_OK);
        } else {
            response.setResponseCode(OC_BUGS);
        }
        return response;
    }

    @Override
    public HttpResponseBody<DeleteCompanyResponseDTO> deleteCompany(DeleteCompanyRequestDTO deleteCompanyRequestDTO) {
        HttpResponseBody<DeleteCompanyResponseDTO> response = new DeleteCompanyResponse();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        try {
            Company company = companyDao.findByCompanyName(deleteCompanyRequestDTO.getCompanyName());
            if (company == null) {
                response.setError("Company not found");
                response.setMessage("Company not found");
            } else {
                Jwt jwt = (Jwt) authentication.getPrincipal();
                String username = jwt.getClaim("username");
                Long userId = userDao.findIdByUsernameOrEmail(username);
                UserCompanyRoles userCompanyRoles = userCompanyRolesdao.findByUserId(userId, company.getCoreEntityId());
                if (userCompanyRoles == null) {
                    response.setError("User not in company");
                    response.setMessage("User not in company");
                } else {
                    if (userCompanyRoles.getRole() != RoleEnum.OWNER) {
                        response.setMessage("User doesnt have enough rights");
                    } else {
                        userCompanyRoles = userCompanyRolesMapper.deleteOwner(userCompanyRoles);
                        company = companyMapper.deleteCompany(company);
                        try {
                            companyTrans.save(company, userCompanyRoles);
                            response.setMessage("Company deleted successfully");
                            response.setResponseEntity(new DeleteCompanyResponseDTO("ваш бизнес разрушен :("));
                        } catch (DataIntegrityViolationException e) {
                            response.setError("Transaction failed");
                            response.setMessage("Data integrity violation: " + e.getMessage());
                            logger.error("Data integrity error while saving company and user role: ", e);
                        } catch (Exception e) {
                            response.setError("Transaction failed");
                            response.setMessage("Unexpected error: " + e.getMessage());
                            logger.error("Unexpected error while saving company and user role: ", e);
                        }
                    }
                }
            }
        } catch (
                JwtException e) {
            response.setError("get user error");
            response.setMessage("get user error");
        }
        if (response.getErrors().isEmpty()) {
            response.setResponseCode(OC_OK);
        } else {
            response.setResponseCode(OC_BUGS);
        }
        return response;
    }

    @Override
    public HttpResponseBody<AllCompaniesResponseDTO> getAllCompanies() {
        HttpResponseBody<AllCompaniesResponseDTO> response = new GetAllCompaniesResponse();
        List<Company> companiesList = companyDao.findAll();
        if (companiesList.isEmpty()) {
            response.setMessage("Companies not found");
            response.setResponseEntity(null);
        } else {
            AllCompaniesResponseDTO allCompaniesResponseDTO = new AllCompaniesResponseDTO(companiesList.stream().map(
                    companyMapper::mapCompanyToGetAllCompaniesResponseDTO).toList());
            response.setResponseEntity(allCompaniesResponseDTO);
            response.setMessage("All companies found");
        }
        if (response.getErrors().isEmpty()) {
            response.setResponseCode(OC_OK);
        } else {
            response.setResponseCode(OC_BUGS);
        }
        return response;
    }


}

