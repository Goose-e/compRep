package com.example.companyReputationManagement.iservice.services.users.service;

import com.example.companyReputationManagement.dao.UserDao;
import com.example.companyReputationManagement.dto.user.dto.create.UserCreateRequestDTO;
import com.example.companyReputationManagement.dto.user.dto.create.UserCreateResponse;
import com.example.companyReputationManagement.dto.user.dto.create.UserCreateResponseDTO;
import com.example.companyReputationManagement.dto.user.dto.login.UserLoginRequestDTO;
import com.example.companyReputationManagement.dto.user.dto.login.UserLoginResponseDTO;
import com.example.companyReputationManagement.httpResponse.HttpResponseBody;
import com.example.companyReputationManagement.iservice.IUserService;
import com.example.companyReputationManagement.mapper.UserMapper;
import com.example.companyReputationManagement.models.CompanyUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.example.companyReputationManagement.constants.SysConst.*;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.I_AM_A_TEAPOT;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {
    private final UserDao userDao;
    private final UserMapper userMapper;

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
        return null;
    }
}
