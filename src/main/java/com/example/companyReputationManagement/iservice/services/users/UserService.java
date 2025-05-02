package com.example.companyReputationManagement.iservice.services.users;

import com.example.companyReputationManagement.dao.UserDao;
import com.example.companyReputationManagement.dto.user.create.UserCreateRequestDTO;
import com.example.companyReputationManagement.dto.user.create.UserCreateResponse;
import com.example.companyReputationManagement.dto.user.create.UserCreateResponseDTO;
import com.example.companyReputationManagement.dto.user.edit.EditUserRequestDTO;
import com.example.companyReputationManagement.dto.user.edit.EditUserResponse;
import com.example.companyReputationManagement.dto.user.edit.EditUserResponseDTO;
import com.example.companyReputationManagement.dto.user.get_by_code.GetUserByCodeResponse;
import com.example.companyReputationManagement.dto.user.get_by_code.GetUserByCodeResponseDTO;
import com.example.companyReputationManagement.dto.user.login.UserLoginRequestDTO;
import com.example.companyReputationManagement.dto.user.login.UserLoginResponse;
import com.example.companyReputationManagement.dto.user.login.UserLoginResponseDTO;
import com.example.companyReputationManagement.httpResponse.HttpResponseBody;
import com.example.companyReputationManagement.iservice.IUserService;
import com.example.companyReputationManagement.jwt.TokenService;
import com.example.companyReputationManagement.mapper.UserMapper;
import com.example.companyReputationManagement.models.CompanyUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtException;
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
        if (editUserRequestDTO.getNewUsername() != null && userDao.findUserByUserName(editUserRequestDTO.getNewUsername()) != null) {
            errorMessage.append("Username is already taken. ");
        }
        if (editUserRequestDTO.getNewEmail() != null && userDao.findIdByUsernameOrEmail(editUserRequestDTO.getNewEmail()) != null) {
            errorMessage.append("Email is already taken. ");
        }

        if (!errorMessage.isEmpty()) {
            response.setMessage(errorMessage.toString().trim());
            response.setError(errorMessage.toString().trim());
            response.setResponseEntity(null);
        } else {
            CompanyUser user = userDao.findUserByUserCode(extractUsernameFromJwt());
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
        String userCode = extractUsernameFromJwt();
        if (userCode == null) {
            response.setMessage("User unauthorized");
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

    private String extractUsernameFromJwt() throws JwtException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = (Jwt) authentication.getPrincipal();
        return jwt.getClaim("userCode");
    }
}
