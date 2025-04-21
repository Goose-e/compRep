package com.example.companyReputationManagement.iservice.services.service.comapany;


import com.example.companyReputationManagement.dao.CompanyDao;
import com.example.companyReputationManagement.dao.UserCompanyRolesDao;
import com.example.companyReputationManagement.dao.UserDao;
import com.example.companyReputationManagement.dto.company.add_user.AddUserRequestDTO;
import com.example.companyReputationManagement.dto.company.add_user.AddUserResponse;
import com.example.companyReputationManagement.dto.company.add_user.AddUserResponseDTO;
import com.example.companyReputationManagement.dto.company.change_status.ChangeCompanyStatus;
import com.example.companyReputationManagement.dto.company.change_status.ChangeCompanyStatusRequestDTO;
import com.example.companyReputationManagement.dto.company.change_status.ChangeCompanyStatusResponseDTO;
import com.example.companyReputationManagement.dto.company.change_user_company_status.ChangeCompanyUserStatusRequestDTO;
import com.example.companyReputationManagement.dto.company.change_user_company_status.ChangeCompanyUserStatusResponse;
import com.example.companyReputationManagement.dto.company.change_user_company_status.ChangeCompanyUserStatusResponseDTO;
import com.example.companyReputationManagement.dto.company.change_user_role.ChangeUserCompanyRoleRequestDTO;
import com.example.companyReputationManagement.dto.company.change_user_role.ChangeUserRoleResponse;
import com.example.companyReputationManagement.dto.company.change_user_role.ChangeUserRoleResponseDTO;
import com.example.companyReputationManagement.dto.company.create.CompanyCreateRequestDTO;
import com.example.companyReputationManagement.dto.company.create.CompanyCreateResponse;
import com.example.companyReputationManagement.dto.company.create.CompanyCreateResponseDTO;
import com.example.companyReputationManagement.dto.company.edit.EditCompanyRequestDTO;
import com.example.companyReputationManagement.dto.company.edit.EditCompanyResponse;
import com.example.companyReputationManagement.dto.company.edit.EditCompanyResponseDTO;
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
import com.example.companyReputationManagement.models.enums.StatusEnum;
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
import java.util.Objects;

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
        try {
            String username = extractUsernameFromJwt();
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
        response.setResponseCode(response.getErrors().isEmpty() ? OC_OK : OC_BUGS);

        return response;
    }

    @Override
    public HttpResponseBody<ChangeCompanyStatusResponseDTO> changeCompanyStatus(ChangeCompanyStatusRequestDTO changeCompanyStatusRequestDTO) {
        HttpResponseBody<ChangeCompanyStatusResponseDTO> response = new ChangeCompanyStatus();
        try {
            Company company = companyDao.findByCompanyName(changeCompanyStatusRequestDTO.getCompanyName());
            if (company == null) {
                response.setError("Company not found");
                response.setMessage("Company not found");
            } else {
                String username = extractUsernameFromJwt();
                Long userId = userDao.findIdByUsernameOrEmail(username);
                UserCompanyRoles userCompanyRoles = userCompanyRolesdao.findByUserId(userId, company.getCoreEntityId());
                if (userCompanyRoles == null) {
                    response.setError("User not in company");
                    response.setMessage("User not in company");
                } else {
                    if (userCompanyRoles.getRole() != RoleEnum.OWNER) {
                        response.setMessage("User doesnt have enough rights");
                    } else {
                        try {
                            userCompanyRoles = userCompanyRolesMapper.changeUserStatus(userCompanyRoles, changeCompanyStatusRequestDTO.getNewStatusId());
                            company = companyMapper.changeCompanyStatus(company, changeCompanyStatusRequestDTO.getNewStatusId());
                            companyTrans.save(company, userCompanyRoles);
                            response.setMessage("Company deleted successfully");
                            StatusEnum newStatus = StatusEnum.fromId(changeCompanyStatusRequestDTO.getNewStatusId().intValue());
                            response.setResponseEntity(new ChangeCompanyStatusResponseDTO(company.getName(), newStatus.getStatus()));
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
        response.setResponseCode(response.getErrors().isEmpty() ? OC_OK : OC_BUGS);

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
        response.setResponseCode(response.getErrors().isEmpty() ? OC_OK : OC_BUGS);

        return response;
    }

    @Override
    public HttpResponseBody<EditCompanyResponseDTO> editCompany(EditCompanyRequestDTO editCompanyRequestDTO) {
        HttpResponseBody<EditCompanyResponseDTO> response = new EditCompanyResponse();
        try {
            Company company = companyDao.findByCompanyCode(editCompanyRequestDTO.getCompanyCode());
            if (company == null) {
                response.setError("Company not found");
                response.setMessage("Company not found");
            } else {
                String username = extractUsernameFromJwt();
                Long userId = userDao.findIdByUsernameOrEmail(username);
                UserCompanyRoles userCompanyRoles = userCompanyRolesdao.findByUserId(userId, company.getCoreEntityId());
                if (userCompanyRoles == null) {
                    response.setError("User not in company");
                    response.setMessage("User not in company");
                } else {
                    if (userCompanyRoles.getRole() != RoleEnum.OWNER && userCompanyRoles.getRole() != RoleEnum.ADMIN) {
                        response.setMessage("User doesnt have enough rights");
                    } else {
                        company = companyMapper.mapCompanyToEditedCompany(company, editCompanyRequestDTO);
                        EditCompanyResponseDTO editCompanyResponseDTO = companyMapper.mapCompanyToEditCompanyResponseDTO(company);
                        try {
                            companyTrans.updateCompany(company);
                            response.setMessage("Company edited successfully");
                            response.setResponseEntity(editCompanyResponseDTO);
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

        response.setResponseCode(response.getErrors().isEmpty() ? OC_OK : OC_BUGS);

        return response;
    }

    @Override
    public HttpResponseBody<ChangeUserRoleResponseDTO> changeUserRole(ChangeUserCompanyRoleRequestDTO changeUserCompanyRoleRequestDTO) {
        HttpResponseBody<ChangeUserRoleResponseDTO> response = new ChangeUserRoleResponse();
        boolean isOwner = false;
        try {
            Company company = companyDao.findByCompanyCode(changeUserCompanyRoleRequestDTO.getCompanyCode());
            if (company == null) {
                response.setError("Company not found");
                response.setMessage("Company not found");
                return response;
            } else {
                String username = extractUsernameFromJwt();
                Long userId = userDao.findIdByUsernameOrEmail(username);
                UserCompanyRoles userCompanyRolesAdmin = userCompanyRolesdao.findByUserId(userId, company.getCoreEntityId());
                if (userCompanyRolesAdmin == null) {
                    response.setError("Users not in company");
                    response.setMessage("Users not in company");
                } else {
                    if (hasPermissionToChangeRole(userCompanyRolesAdmin.getRole())) {
                        response.setMessage("User doesnt have enough rights");
                    } else {
                        Long userCandidateId = userDao.findIdByUsernameOrEmail(changeUserCompanyRoleRequestDTO.getUsername());
                        if (userCandidateId == null) {
                            response.setError("User not found");
                            response.setMessage("User not found");
                        } else {
                            UserCompanyRoles userCompanyRolesCandidate = userCompanyRolesdao.findByUserId(userCandidateId, company.getCoreEntityId());
                            if (userCompanyRolesCandidate == null) {
                                response.setMessage("Users not in company");
                            } else {
                                Long newRole = changeUserCompanyRoleRequestDTO.getNewRoleId();
                                if (newRole.intValue() == 2) {
                                    if (userCompanyRolesAdmin.getRole() == RoleEnum.OWNER) {
                                        userCompanyRolesAdmin.setRole(RoleEnum.ADMIN);
                                        isOwner = true;
                                    } else {
                                        response.setMessage("User doesnt have enough rights");
                                    }
                                }
                                userCompanyRolesCandidate.setRole(RoleEnum.fromId(newRole.intValue()));
                                try {
                                    if (isOwner) {
                                        companyTrans.changeUserRoleOwner(userCompanyRolesAdmin, userCompanyRolesCandidate);
                                        response.setMessage("User role owner successfully");
                                    } else {
                                        companyTrans.saveCompanyUserRole(userCompanyRolesCandidate);
                                        response.setMessage("User role successfully");
                                    }
                                    ChangeUserRoleResponseDTO changeUserRoleResponseDTO = userCompanyRolesMapper.changeUserRole(userCompanyRolesCandidate, changeUserCompanyRoleRequestDTO.getUsername());
                                    response.setResponseEntity(changeUserRoleResponseDTO);
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
                }
            }
        } catch (
                JwtException e) {
            response.setError("get user error");
            response.setMessage("get user error");
        }

        response.setResponseCode(response.getErrors().isEmpty() ? OC_OK : OC_BUGS);

        return response;
    }

    @Override
    public HttpResponseBody<AddUserResponseDTO> addUser(AddUserRequestDTO addUserRequestDTO) {
        HttpResponseBody<AddUserResponseDTO> response = new AddUserResponse();
        try {
            Company company = companyDao.findByCompanyCode(addUserRequestDTO.getCompanyCode());
            if (company == null) {
                response.setError("Company not found");
                response.setMessage("Company not found");
            } else {
                Long userId = userDao.findIdByUsernameOrEmail(extractUsernameFromJwt());
                UserCompanyRoles userCompanyRolesAdmin = userCompanyRolesdao.findByUserId(userId, company.getCoreEntityId());
                if (userCompanyRolesAdmin == null) {
                    response.setError("Users not in company");
                    response.setMessage("Users not in company");
                } else {
                    if (hasPermissionToChangeRole(userCompanyRolesAdmin.getRole())) {
                        response.setMessage("User doesnt have enough rights");
                    } else {
                        Long userCandidateId = userDao.findIdByUsernameOrEmail(addUserRequestDTO.getUsername());
                        if (userCandidateId == null) {
                            response.setError("User not found");
                            response.setMessage("User not found");
                        } else {
                            UserCompanyRoles userCompanyRolesCandidate = userCompanyRolesdao.findByUserId(userCandidateId, company.getCoreEntityId());
                            if (userCompanyRolesCandidate != null) {
                                response.setMessage("Users already in company");
                            } else {
                                userCompanyRolesCandidate = userCompanyRolesMapper.addUser(userCandidateId, company.getCoreEntityId());
                                try {
                                    companyTrans.saveCompanyUserRole(userCompanyRolesCandidate);
                                    AddUserResponseDTO addUserResponseDTO = userCompanyRolesMapper.mapNewUserToAddUserResponse(addUserRequestDTO.getUsername());
                                    response.setResponseEntity(addUserResponseDTO);
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
                }
            }
        } catch (
                JwtException e) {
            response.setError("get user error");
            response.setMessage("get user error");
        }

        response.setResponseCode(response.getErrors().isEmpty() ? OC_OK : OC_BUGS);

        return response;
    }

    @Override
    public HttpResponseBody<ChangeCompanyUserStatusResponseDTO> changeUserStatus(ChangeCompanyUserStatusRequestDTO changeCompanyUserStatusRequestDTO) {
        HttpResponseBody<ChangeCompanyUserStatusResponseDTO> response = new ChangeCompanyUserStatusResponse();
        try {
            Company company = companyDao.findByCompanyName(changeCompanyUserStatusRequestDTO.getCompanyName());
            if (company == null) {
                response.setError("Company not found");
                response.setMessage("Company not found");
            } else {
                String username = extractUsernameFromJwt();
                Long userId = userDao.findIdByUsernameOrEmail(username);
                UserCompanyRoles userCompanyRoles = userCompanyRolesdao.findByUserId(userId, company.getCoreEntityId());
                if (userCompanyRoles == null) {
                    response.setError("User not in company");
                    response.setMessage("User not in company");
                } else {
                    if (hasPermissionToChangeRole(userCompanyRoles.getRole())) {
                        response.setMessage("User doesnt have enough rights");
                    } else {
                        try {
                            Long userCandidateId = userDao.findIdByUsernameOrEmail(changeCompanyUserStatusRequestDTO.getUsername());
                            if (Objects.equals(userCandidateId, userId)) {
                                response.setMessage("You cant ban yourself");
                            } else {
                                UserCompanyRoles userCandidate = userCompanyRolesdao.findByUserId(userCandidateId, company.getCoreEntityId());
                                if (userCandidate == null) {
                                    response.setError("User not in company");
                                } else {
                                    if (!hasHigherRole(userCompanyRoles.getRole(),userCandidate.getRole())) {
                                        response.setMessage("You cant ban admin or owner");
                                    } else {
                                        userCandidate = userCompanyRolesMapper.changeUserStatus(userCandidate, changeCompanyUserStatusRequestDTO.getNewStatusId());
                                        companyTrans.saveCompanyUserRole(userCandidate);
                                        response.setMessage("User changed status successfully");
                                        StatusEnum newStatus = StatusEnum.fromId(changeCompanyUserStatusRequestDTO.getNewStatusId().intValue());
                                        response.setResponseEntity(new ChangeCompanyUserStatusResponseDTO(changeCompanyUserStatusRequestDTO.getUsername(), newStatus.getStatus()));
                                    }
                                }
                            }
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
        response.setResponseCode(response.getErrors().isEmpty() ? OC_OK : OC_BUGS);

        return response;
    }

    private String extractUsernameFromJwt() throws JwtException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = (Jwt) authentication.getPrincipal();
        return jwt.getClaim("username");
    }

    private boolean hasPermissionToChangeRole(RoleEnum currentRole) {
        return !currentRole.equals(RoleEnum.OWNER) && !currentRole.equals(RoleEnum.ADMIN);
    }
    private boolean hasHigherRole(RoleEnum current, RoleEnum target) {
        if (current == null || target == null) return false;
        return current.getId() > target.getId();
    }
}

