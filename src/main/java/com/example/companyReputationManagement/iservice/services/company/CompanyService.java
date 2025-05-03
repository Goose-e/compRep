package com.example.companyReputationManagement.iservice.services.company;

import com.example.companyReputationManagement.dao.CompanyDao;
import com.example.companyReputationManagement.dao.CompanySourceUrlDao;
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
import com.example.companyReputationManagement.dto.company.get_all.AllCompaniesResponseListDTO;
import com.example.companyReputationManagement.dto.company.get_all.GetAllCompaniesResponse;
import com.example.companyReputationManagement.dto.company.get_all.GetAllCompaniesResponseDTO;
import com.example.companyReputationManagement.dto.company.get_all_company_users.GetAllCompanyUsersRequestDTO;
import com.example.companyReputationManagement.dto.company.get_all_company_users.GetAllCompanyUsersResponse;
import com.example.companyReputationManagement.dto.company.get_all_company_users.GetAllCompanyUsersResponseDTO;
import com.example.companyReputationManagement.dto.company.get_all_company_users.GetAllCompanyUsersResponseListDTO;
import com.example.companyReputationManagement.dto.company.get_all_user_companies.AllUserCompaniesResponseListDTO;
import com.example.companyReputationManagement.dto.company.get_all_user_companies.GetAllUserCompaniesResponse;
import com.example.companyReputationManagement.dto.company.get_all_user_companies.GetAllUserCompaniesResponseDTO;
import com.example.companyReputationManagement.dto.company.get_by_code.GetCompanyByCodeRequestDTO;
import com.example.companyReputationManagement.dto.company.get_by_code.GetCompanyByCodeResponse;
import com.example.companyReputationManagement.dto.company.get_by_code.GetCompanyByCodeResponseDTO;
import com.example.companyReputationManagement.httpResponse.HttpResponseBody;
import com.example.companyReputationManagement.iservice.ICompanyService;
import com.example.companyReputationManagement.iservice.IJwtService;
import com.example.companyReputationManagement.iservice.services.transactions.CompanyTrans;
import com.example.companyReputationManagement.mapper.CompanyMapper;
import com.example.companyReputationManagement.mapper.CompanySourceUrlMapper;
import com.example.companyReputationManagement.mapper.UserCompanyRolesMapper;
import com.example.companyReputationManagement.models.Company;
import com.example.companyReputationManagement.models.CompanySourceUrl;
import com.example.companyReputationManagement.models.CompanyUser;
import com.example.companyReputationManagement.models.UserCompanyRoles;
import com.example.companyReputationManagement.models.enums.RoleEnum;
import com.example.companyReputationManagement.models.enums.SourcesEnum;
import com.example.companyReputationManagement.models.enums.StatusEnum;
import lombok.RequiredArgsConstructor;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

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
    private final CompanySourceUrlMapper companySourceUrlMapper;
    private final CompanySourceUrlDao companySourceUrlDao;
    private final IJwtService jwtService;

    //Парсинг
    private WebDriver createWebDriver() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--blink-settings=imagesEnabled=false");
        options.addArguments("--disable-extensions");
        options.addArguments("--disable-popup-blocking");
        options.addArguments("--disable-default-apps");
        options.addArguments("--headless", "--disable-gpu", "--window-size=1920x1080");
        return new ChromeDriver(options);
    }

    private String findCompanyUrl(String companyName, SourcesEnum source) {
        WebDriver driver = createWebDriver();
        try {
            driver.get(source.getUrl());

            WebElement input = driver.findElement(By.cssSelector("input.is-search-input"));
            input.sendKeys(companyName);

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(3));
            WebElement firstSuggestion = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".is-title a")));

            return firstSuggestion.getAttribute("href");
        } catch (Exception e) {
            return null;
        } finally {
            driver.quit();
        }
    }

    //Проверки
    private boolean checkStatus(StatusEnum userStatus) {
        return userStatus == StatusEnum.CLOSED;
    }

    private boolean hasPermissionToChangeRole(RoleEnum currentRole) {
        return !currentRole.equals(RoleEnum.OWNER) && !currentRole.equals(RoleEnum.ADMIN);
    }

    private boolean hasHigherRole(RoleEnum current, RoleEnum target) {
        if (current == null || target == null) return false;
        return current.getId() > target.getId();
    }


    @Override
    public HttpResponseBody<CompanyCreateResponseDTO> createCompany(CompanyCreateRequestDTO companyCreateRequestDTO) {
        HttpResponseBody<CompanyCreateResponseDTO> response = new CompanyCreateResponse();
        try {
            CompanyUser user = userDao.findUserByUserCode(jwtService.extractUserCodeFromJwt());
            if (user != null) {
                boolean companyExists = companyDao.existByCompanyName(companyCreateRequestDTO.getCompanyName());
                if (!companyExists) {
                    response.setMessage("Company is being created...");
                    response.setResponseCode(OC_OK);
                    Company company = companyMapper.mapCompanyCreateRequestDTOToCompany(companyCreateRequestDTO);
                    response.setResponseEntity(companyMapper.mapCompanyCreateRequestDTOToCompanyCreateResponse(company, "in process"));
                    UserCompanyRoles userCompanyRoles = userCompanyRolesMapper.mapUserAndCompanyToUserCompanyRoles(user, company);
                    companyTrans.saveCompanyAndRole(company, userCompanyRoles);
                    CompletableFuture.runAsync(() -> {
                        try {
                            String url = findCompanyUrl(companyCreateRequestDTO.getCompanyName(), SourcesEnum.OTZOVIK);
                            CompanySourceUrl companySourceUrl = companySourceUrlMapper.create(company.getCoreEntityId(), url);
                            try {
                                companyTrans.saveCompanySourceUrl(companySourceUrl);
                            } catch (DataIntegrityViolationException e) {
                                logger.error("Data integrity error while saving company and user role: ", e);
                            } catch (Exception e) {
                                logger.error("Unexpected error while saving company and user role: ", e);
                            }
                        } catch (Exception e) {
                            logger.error("Error occurred while creating company in background task: ", e);
                        }
                    });
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
            Company company = companyDao.findByCompanyCode(changeCompanyStatusRequestDTO.getCompanyCode());
            if (company == null) {
                response.setError("Company not found");
                response.setMessage("Company not found");
            } else {

                Long userId = userDao.findUserIdByUserCode(jwtService.extractUserCodeFromJwt());
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
                            companyTrans.saveCompanyAndRole(company, userCompanyRoles);
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
    public HttpResponseBody<AllCompaniesResponseListDTO> getAllCompanies() {
        HttpResponseBody<AllCompaniesResponseListDTO> response = new GetAllCompaniesResponse();
        List<GetAllCompaniesResponseDTO> companiesList = companyDao.findAllWithUrls();
        if (companiesList.isEmpty()) {
            response.setMessage("Companies not found");
            response.setResponseEntity(null);
        } else {
            AllCompaniesResponseListDTO allCompaniesResponseListDTO = new AllCompaniesResponseListDTO(companiesList);
            response.setResponseEntity(allCompaniesResponseListDTO);
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
                Long userId = userDao.findUserIdByUserCode(jwtService.extractUserCodeFromJwt());
                UserCompanyRoles userCompanyRoles = userCompanyRolesdao.findByUserId(userId, company.getCoreEntityId());
                if (userCompanyRoles == null) {
                    response.setError("User not in company");
                    response.setMessage("User not in company");
                } else {
                    if (checkStatus(userCompanyRoles.getStatus())) {
                        response.setMessage("You banned");
                    } else {
                        if (userCompanyRoles.getRole() != RoleEnum.OWNER && userCompanyRoles.getRole() != RoleEnum.ADMIN) {
                            response.setMessage("User doesnt have enough rights");
                        } else {
                            company = companyMapper.mapCompanyToEditedCompany(company, editCompanyRequestDTO);
                            CompanySourceUrl companySourceUrl = companySourceUrlDao.findByCompanyId(company.getCoreEntityId());
                            companySourceUrl = companySourceUrlMapper.edit(editCompanyRequestDTO.getNewOtzovikUrl(), companySourceUrl);
                            EditCompanyResponseDTO editCompanyResponseDTO = companyMapper.mapCompanyToEditCompanyResponseDTO(company, companySourceUrl);
                            try {
                                companyTrans.updateCompany(company, companySourceUrl);
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
            Company company = companyDao.findByCompanyCode(jwtService.extractUserCodeFromJwt());
            if (company == null) {
                response.setError("Company not found");
                response.setMessage("Company not found");
            } else {
                Long userId = userDao.findUserIdByUserCode(jwtService.extractUserCodeFromJwt());
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
                                        userCompanyRolesCandidate.setStatus(StatusEnum.ACTUAL);
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
        } catch (JwtException e) {
            response.setError("Get user error");
            response.setMessage("Get user error");
        }

        response.setResponseCode(response.getErrors().isEmpty() ? OC_OK : OC_BUGS);
        return response;
    }

    @Override
    public HttpResponseBody<AddUserResponseDTO> addUser(AddUserRequestDTO addUserRequestDTO) {
        HttpResponseBody<AddUserResponseDTO> response = new AddUserResponse();
        try {
            Company company = companyDao.findByCompanyCode(jwtService.extractUserCodeFromJwt());
            if (company == null) {
                response.setError("Company not found");
                response.setMessage("Company not found");
            } else {
                Long userId = userDao.findUserIdByUserCode(jwtService.extractUserCodeFromJwt());
                UserCompanyRoles userCompanyRolesAdmin = userCompanyRolesdao.findByUserId(userId, company.getCoreEntityId());
                if (userCompanyRolesAdmin == null) {
                    response.setError("Users not in company");
                    response.setMessage("Users not in company");
                } else {
                    if (checkStatus(userCompanyRolesAdmin.getStatus())) {
                        response.setError("User banned");
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
                                        response.setMessage("User added successfully");
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
            Company company = companyDao.findByCompanyCode(changeCompanyUserStatusRequestDTO.getCompanyCode());
            if (company == null) {
                response.setError("Company not found");
                response.setMessage("Company not found");
            } else {
                Long userId = userDao.findUserIdByUserCode(jwtService.extractUserCodeFromJwt());
                UserCompanyRoles userCompanyRoles = userCompanyRolesdao.findByUserId(userId, company.getCoreEntityId());
                if (userCompanyRoles == null) {
                    response.setError("User not in company");
                    response.setMessage("User not in company");
                } else {
                    if (checkStatus(userCompanyRoles.getStatus())) {
                        response.setMessage("User already has enough rights");
                    } else {
                        if (hasPermissionToChangeRole(userCompanyRoles.getRole())) {
                            response.setMessage("User doesnt have enough rights");
                        } else {
                            try {
                                CompanyUser userCandidateForId = userDao.findUserByUserCode(changeCompanyUserStatusRequestDTO.getUserCode());
                                Long userCandidateId = userCandidateForId.getCoreEntityId();
                                if (Objects.equals(userCandidateId, userId)) {
                                    response.setMessage("You cant ban yourself");
                                } else {
                                    UserCompanyRoles userCandidate = userCompanyRolesdao.findByUserId(userCandidateId, company.getCoreEntityId());
                                    if (userCandidate == null) {
                                        response.setError("User not in company");
                                    } else {
                                        if (!hasHigherRole(userCompanyRoles.getRole(), userCandidate.getRole())) {
                                            response.setMessage("You cant ban admin or owner");
                                        } else {
                                            userCandidate = userCompanyRolesMapper.changeUserStatus(userCandidate, changeCompanyUserStatusRequestDTO.getNewStatusId());
                                            companyTrans.saveCompanyUserRole(userCandidate);
                                            response.setMessage("User changed status successfully");
                                            StatusEnum newStatus = StatusEnum.fromId(changeCompanyUserStatusRequestDTO.getNewStatusId().intValue());
                                            response.setResponseEntity(new ChangeCompanyUserStatusResponseDTO(userCandidateForId.getUsername(), newStatus.getStatus()));
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
    public HttpResponseBody<GetCompanyByCodeResponseDTO> getCompanyByCode(GetCompanyByCodeRequestDTO getCompanyByCodeRequestDTO) {
        HttpResponseBody<GetCompanyByCodeResponseDTO> response = new GetCompanyByCodeResponse();
        String jwt = jwtService.extractUserCodeFromJwt();

        GetCompanyByCodeResponseDTO company = companyDao.findCompanyByCodeWithUrls(getCompanyByCodeRequestDTO.companyCode(), jwt);
        if (company == null) {
            response.setMessage("Company not found");
            response.setResponseEntity(null);
        } else {
            response.setResponseEntity(company);
            response.setMessage("Company found");
        }
        response.setResponseCode(response.getErrors().isEmpty() ? OC_OK : OC_BUGS);

        return response;
    }

    @Override
    public HttpResponseBody<AllUserCompaniesResponseListDTO> getAllUserCompanies() {
        HttpResponseBody<AllUserCompaniesResponseListDTO> response = new GetAllUserCompaniesResponse();
        String code = jwtService.extractUserCodeFromJwt();
        if (code == null) {
            response.setError("User not authorized");
        } else {
            List<GetAllUserCompaniesResponseDTO> companiesList = companyDao.findAllUsersCompanies(code);
            if (companiesList.isEmpty()) {
                response.setMessage("Companies not found");
            } else {
                AllUserCompaniesResponseListDTO allCompaniesResponseListDTO = new AllUserCompaniesResponseListDTO(companiesList);
                response.setResponseEntity(allCompaniesResponseListDTO);
                response.setMessage("All companies found");
            }
        }
        response.setResponseCode(response.getErrors().isEmpty() ? OC_OK : OC_BUGS);

        return response;
    }

    @Override
    public HttpResponseBody<GetAllCompanyUsersResponseListDTO> getAllCompanyUsers(GetAllCompanyUsersRequestDTO getAllCompanyUsersRequestDTO) {
        HttpResponseBody<GetAllCompanyUsersResponseListDTO> response = new GetAllCompanyUsersResponse();
        List<GetAllCompanyUsersResponseDTO> companyUsersList = companyDao.findAllCompanyUsers(getAllCompanyUsersRequestDTO.companyCode());
        if (companyUsersList.isEmpty()) {
            response.setMessage("Company users not found");
            response.setError("No users in company");
        } else {
            response.setMessage("Company users found");
            response.setResponseEntity(new GetAllCompanyUsersResponseListDTO(companyUsersList));
        }
        response.setResponseCode(response.getErrors().isEmpty() ? OC_OK : OC_BUGS);
        return response;
    }


}

