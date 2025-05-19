package com.example.companyReputationManagement.iservice;

import com.example.companyReputationManagement.dto.user.create.UserCreateRequestDTO;
import com.example.companyReputationManagement.dto.user.create.UserCreateResponseDTO;
import com.example.companyReputationManagement.dto.user.edit.EditUserRequestDTO;
import com.example.companyReputationManagement.dto.user.edit.EditUserResponseDTO;
import com.example.companyReputationManagement.dto.user.find.FindByNameRequestDTO;
import com.example.companyReputationManagement.dto.user.find.FindByNameResponseListDTO;
import com.example.companyReputationManagement.dto.user.get_by_code.GetUserByCodeResponseDTO;
import com.example.companyReputationManagement.dto.user.login.UserLoginRequestDTO;
import com.example.companyReputationManagement.dto.user.login.UserLoginResponseDTO;
import com.example.companyReputationManagement.httpResponse.HttpResponseBody;

public interface IUserService {
    HttpResponseBody<UserCreateResponseDTO> register(UserCreateRequestDTO userCreateRequestDTO);

    HttpResponseBody<UserLoginResponseDTO> login(UserLoginRequestDTO userLoginRequestDTO);

    HttpResponseBody<EditUserResponseDTO> edit(EditUserRequestDTO editUserRequestDTO);

    HttpResponseBody<GetUserByCodeResponseDTO> getUserByCode();

    HttpResponseBody<FindByNameResponseListDTO> findByName(FindByNameRequestDTO findByNameRequestDTO);
}
