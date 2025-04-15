package com.example.companyReputationManagement.iservice.services.service.comapany;


import com.example.companyReputationManagement.dao.CompanyDao;
import com.example.companyReputationManagement.dao.UserDao;
import com.example.companyReputationManagement.dto.company.create.CompanyCreateRequestDTO;
import com.example.companyReputationManagement.dto.company.create.CompanyCreateResponse;
import com.example.companyReputationManagement.dto.company.create.CompanyCreateResponseDTO;
import com.example.companyReputationManagement.httpResponse.HttpResponseBody;
import com.example.companyReputationManagement.iservice.ICompanyService;
import com.example.companyReputationManagement.mapper.CompanyMapper;
import com.example.companyReputationManagement.models.Company;
import com.example.companyReputationManagement.models.CompanyUser;
import com.example.companyReputationManagement.models.enums.RoleEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import static com.example.companyReputationManagement.constants.SysConst.*;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@Service
@RequiredArgsConstructor
public class CompanyService implements ICompanyService {
    private final CompanyDao companyDao;
    private final CompanyMapper companyMapper;
    private final UserDao userDao;

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
                    if (companyDao.save(company) != null) {
                        user.setRoleRefId(RoleEnum.ADMIN);
                        user.setCompanyId(company.getCoreEntityId());
                        userDao.save(user);
                        response.setMessage("Company created successfully.User role changed successfully.");
                        response.setResponseEntity(companyMapper.mapCompanyToCompanyCreateResponseDTO(company));
                    } else {
                        response.setMessage("save company failed");
                        response.setError("save company failed");
                        response.addErrorInfo(CREATE_COMPANY_ERROR, String.valueOf(INTERNAL_SERVER_ERROR), "save company failed");
                    }

                } else {
                    response.setMessage("Company name already exist");
                    response.setResponseEntity(null);
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
}
