package com.example.companyReputationManagement.controllers;

import com.example.companyReputationManagement.dto.user.login.UserLoginRequestDTO;
import com.example.companyReputationManagement.dto.user.login.UserLoginResponseDTO;
import com.example.companyReputationManagement.iservice.IUserService;
import com.example.companyReputationManagement.dto.user.create.UserCreateRequestDTO;
import com.example.companyReputationManagement.dto.user.create.UserCreateResponseDTO;
import com.example.companyReputationManagement.httpResponse.HttpResponseBody;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@CrossOrigin(maxAge = 3600)
@RestController
@RequestMapping("/auth")
public class AuthController {
    private final IUserService userService;

    @PostMapping(value = "/login")
    public HttpResponseBody<UserLoginResponseDTO> authenticate(@Valid @RequestBody UserLoginRequestDTO userLoginRequestDTO) {
        return userService.login(userLoginRequestDTO);
    }

    @PostMapping(value = "/signup")
    public HttpResponseBody<UserCreateResponseDTO> register(@Valid @RequestBody UserCreateRequestDTO userCreateRequestDTO) {
        return userService.register(userCreateRequestDTO);
    }
}
