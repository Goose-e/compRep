package com.example.companyReputationManagement.iservice;

import com.example.companyReputationManagement.dto.user.dto.create.UserCreateRequestDTO;
import com.example.companyReputationManagement.dto.user.dto.create.UserCreateResponseDTO;
import com.example.companyReputationManagement.dto.user.dto.login.UserLoginRequestDTO;
import com.example.companyReputationManagement.dto.user.dto.login.UserLoginResponseDTO;
import com.example.companyReputationManagement.httpResponse.HttpResponseBody;

public interface IUserService {
     HttpResponseBody<UserCreateResponseDTO> register(UserCreateRequestDTO userCreateRequestDTO);
     HttpResponseBody<UserLoginResponseDTO> login(UserLoginRequestDTO userLoginRequestDTO);
}
