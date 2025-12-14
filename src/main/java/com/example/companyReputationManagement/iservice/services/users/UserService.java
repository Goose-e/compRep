package com.example.companyReputationManagement.iservice.services.users;

import com.example.companyReputationManagement.dao.UserDao;
import com.example.companyReputationManagement.dto.user.ban.BanUserResponse;
import com.example.companyReputationManagement.dto.user.ban.BanUserResponseDTO;
import com.example.companyReputationManagement.dto.user.create.UserCreateRequestDTO;
import com.example.companyReputationManagement.dto.user.create.UserCreateResponse;
import com.example.companyReputationManagement.dto.user.create.UserCreateResponseDTO;
import com.example.companyReputationManagement.dto.user.edit.EditUserRequestDTO;
import com.example.companyReputationManagement.dto.user.edit.EditUserResponse;
import com.example.companyReputationManagement.dto.user.edit.EditUserResponseDTO;
import com.example.companyReputationManagement.dto.user.find.FindByNameRequestDTO;
import com.example.companyReputationManagement.dto.user.find.FindByNameResponse;
import com.example.companyReputationManagement.dto.user.find.FindByNameResponseListDTO;
import com.example.companyReputationManagement.dto.user.get_by_code.GetUserByCodeResponse;
import com.example.companyReputationManagement.dto.user.get_by_code.GetUserByCodeResponseDTO;
import com.example.companyReputationManagement.dto.user.login.UserLoginRequestDTO;
import com.example.companyReputationManagement.dto.user.login.UserLoginResponse;
import com.example.companyReputationManagement.dto.user.login.UserLoginResponseDTO;
import com.example.companyReputationManagement.httpResponse.HttpResponseBody;
import com.example.companyReputationManagement.iservice.IJwtService;
import com.example.companyReputationManagement.iservice.IUserService;
import com.example.companyReputationManagement.mapper.UserMapper;
import com.example.companyReputationManagement.models.CompanyUser;
import com.example.companyReputationManagement.models.enums.StatusEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.companyReputationManagement.constants.SysConst.*;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.I_AM_A_TEAPOT;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {
    private final UserDao userDao;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final IJwtService tokenService;

    @Override
    public HttpResponseBody<UserCreateResponseDTO> register(UserCreateRequestDTO userCreateRequestDTO) {
        HttpResponseBody<UserCreateResponseDTO> response = new UserCreateResponse();
        CompanyUser user = userDao.findUserByLoginOrEmail(userCreateRequestDTO.getUsername(), userCreateRequestDTO.getEmail());
        if (user == null) {
            user = userMapper.mapUserDtoToUser(userCreateRequestDTO);
            if (user != null) {
                user = userDao.save(user);
                response.setMessage("User registered successfully");
                response.setResponseEntity(userMapper.mapUserToUserDtoResponse(user));
            } else {
                response.setMessage("User registration failed");
                response.setResponseEntity(null);
                response.addErrorInfo(SAVE_USER_ERROR, String.valueOf(INTERNAL_SERVER_ERROR), "User not saved");
            }

        } else {
            if (user.getUsername().equals(userCreateRequestDTO.getUsername())) {
                response.setMessage("Username is already taken");
            } else {
                response.setMessage("Email is already taken");
            }
            response.addErrorInfo(CREATE_USER_ERROR, String.valueOf(I_AM_A_TEAPOT), "User exists");
        }
        if (response.getErrors().isEmpty()) {
            response.setResponseCode(OC_OK);
        } else {
            response.setResponseCode(OC_BUGS);
        }
        return response;
    }

    @Override
    public HttpResponseBody<UserLoginResponseDTO> login(UserLoginRequestDTO userLoginRequestDTO) {
        HttpResponseBody<UserLoginResponseDTO> response = new UserLoginResponse();
        CompanyUser user = userDao.findUserByLoginOrEmail(userLoginRequestDTO.getUsernameOrEmail());
        if (user.getStatus() == StatusEnum.CLOSED) {
            response.setMessage("User banned");
            response.setResponseCode(USER_BANNED);
            return response;
        }
        if (user != null) {
            if (passwordEncoder.matches(userLoginRequestDTO.getPassword(), user.getPasswordHash())) {
                response.setMessage("User logged in successfully");
                List<OAuth2AccessToken> tokens = tokenService.createTokens(user.getUserCode(), user.getUsername(), user.getRoleRefId());
                response.setResponseEntity(userMapper.mapTokensToUserLoginResponseDTO(tokens));
            } else {
                response.setMessage("Incorrect password");
            }
        } else {
            response.setMessage("User not found");
        }
        if (response.getErrors().isEmpty()) {
            response.setResponseCode(OC_OK);
        } else {
            response.setResponseCode(OC_BUGS);
        }

        return response;
    }

    @Override
    public HttpResponseBody<EditUserResponseDTO> edit(EditUserRequestDTO editUserRequestDTO) {
        HttpResponseBody<EditUserResponseDTO> response = new EditUserResponse();
        StringBuilder errorMessage = new StringBuilder();
        CompanyUser companyUser = userDao.findUserByLoginOrEmail(editUserRequestDTO.getNewUsername(), editUserRequestDTO.getNewEmail());
        if (companyUser != null) {
            if (editUserRequestDTO.getNewUsername() != null &&
                    editUserRequestDTO.getNewUsername().equals(companyUser.getUsername())) {
                errorMessage.append("Username is already taken. ");
            }

            if (editUserRequestDTO.getNewEmail() != null &&
                    editUserRequestDTO.getNewEmail().equals(companyUser.getEmail())) {
                errorMessage.append("Email is already taken. ");
            }
        }

        if (!errorMessage.isEmpty()) {
            response.setMessage(errorMessage.toString().trim());
            response.setError(errorMessage.toString().trim());
            response.setResponseEntity(null);
        } else {
            CompanyUser user = userDao.findUserByUserCode(tokenService.extractUserCodeFromJwt());
            if (user == null) {
                response.setMessage("User not found");
                response.setResponseEntity(null);
                response.setError("User not found");
            } else {
                user = userMapper.mapUserDtoToUser(editUserRequestDTO, user);
                userDao.save(user);
                response.setMessage("User updated successfully");
                response.setResponseEntity(userMapper.mapUserToUserResponse(user));
            }
        }
        response.setResponseCode(response.getErrors().isEmpty() ? OC_OK : OC_BUGS);
        return response;
    }

    @Override
    public HttpResponseBody<GetUserByCodeResponseDTO> getUserByCode() {
        HttpResponseBody<GetUserByCodeResponseDTO> response = new GetUserByCodeResponse();
        String userCode = tokenService.extractUserCodeFromJwt();
        if (userCode == null) {
            response.setMessage("User unauthorized");
            response.setError("User unauthorized");
        } else {
            CompanyUser user = userDao.findUserByUserCode(userCode);
            if (user == null) {
                response.setMessage("User not found");
                response.setError("User not found");
            } else {
                GetUserByCodeResponseDTO getUserByCodeResponseDTO = userMapper.mapUserToUserDto(user);
                response.setMessage("User found successfully");
                response.setResponseEntity(getUserByCodeResponseDTO);
            }
        }
        response.setResponseCode(response.getErrors().isEmpty() ? OC_OK : OC_BUGS);
        return response;
    }

    @Override
    public HttpResponseBody<FindByNameResponseListDTO> findByName(FindByNameRequestDTO findByNameRequestDTO) {
        HttpResponseBody<FindByNameResponseListDTO> response = new FindByNameResponse();
        String userCode = tokenService.extractUserCodeFromJwt();
        if (userCode == null) {
            response.setMessage("User unauthorized");
            response.setError("User unauthorized");
        } else {
            List<CompanyUser> users = userDao.findUsersByUserName(findByNameRequestDTO.username());
            if (users.isEmpty()) {
                response.setMessage("Users not found");
                response.setError("Users not found");
            } else {
                FindByNameResponseListDTO usersDtoList = userMapper.mapFindByNameResponseListDTO(users);
                response.setMessage("User found successfully");
                response.setResponseEntity(usersDtoList);
            }
        }
        response.setResponseCode(response.getErrors().isEmpty() ? OC_OK : OC_BUGS);
        return response;
    }

    @Override
    public HttpResponseBody<BanUserResponseDTO> banUser() {
        HttpResponseBody<BanUserResponseDTO> response = new BanUserResponse();
        String userCode = tokenService.extractUserCodeFromJwt();
        if (userCode == null) {
            response.setMessage("User unauthorized");
            response.setError("User unauthorized");
        } else {
            CompanyUser user = userDao.findUserByUserCode(userCode);
            if (user == null) {
                response.setMessage("User not found");
                response.setError("User not found");
            } else {
                user = userMapper.chageUserStatus(user);
                System.out.println(user.getStatus());
                userDao.save(user);
                BanUserResponseDTO banUserResponseDTO = new BanUserResponseDTO(user.getUsername());
                response.setMessage(user.getUsername() + " User found successfully");
                response.setResponseEntity(banUserResponseDTO);
            }
        }
        response.setResponseCode(response.getErrors().isEmpty() ? OC_OK : OC_BUGS);
        return response;
    }


}
