package com.example.companyReputationManagement.iservice.services.users.service;

import com.example.companyReputationManagement.dao.UserDao;
import com.example.companyReputationManagement.dto.user.create.UserCreateRequestDTO;
import com.example.companyReputationManagement.dto.user.create.UserCreateResponse;
import com.example.companyReputationManagement.dto.user.create.UserCreateResponseDTO;
import com.example.companyReputationManagement.dto.user.login.UserLoginRequestDTO;
import com.example.companyReputationManagement.dto.user.login.UserLoginResponse;
import com.example.companyReputationManagement.dto.user.login.UserLoginResponseDTO;
import com.example.companyReputationManagement.httpResponse.HttpResponseBody;
import com.example.companyReputationManagement.iservice.IUserService;
import com.example.companyReputationManagement.jwt.TokenService;
import com.example.companyReputationManagement.mapper.UserMapper;
import com.example.companyReputationManagement.models.CompanyUser;
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
    private final TokenService tokenService;

    @Override
    public HttpResponseBody<UserCreateResponseDTO> register(UserCreateRequestDTO userCreateRequestDTO) {
        HttpResponseBody<UserCreateResponseDTO> response = new UserCreateResponse();
        CompanyUser user = userDao.findUserByLoginOrEmail(userCreateRequestDTO.getUsername(), userCreateRequestDTO.getEmail());
        if (user == null) {
            user = userMapper.mapUserDtoToUser(userCreateRequestDTO);
            user = userDao.save(user);
            if (user != null) {
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
        if (user != null) {
            if (passwordEncoder.matches(userLoginRequestDTO.getPassword(), user.getPasswordHash())) {
                response.setMessage("User logged in successfully");
                List<OAuth2AccessToken> tokens = tokenService.createTokens(user.getUserCode(), user.getUsername(), user.getRoleRefId());
                response.setResponseEntity(userMapper.mapTokensToUserLoginResponseDTO(tokens));
            } else {
                System.out.println(user.getPasswordHash());
                response.setMessage("Incorrect password");
            }
        }else {
            response.setMessage("User not found");
        }
        if (response.getErrors().isEmpty()) {
            response.setResponseCode(OC_OK);
        } else {
            response.setResponseCode(OC_BUGS);
        }
        return response;
    }
}
