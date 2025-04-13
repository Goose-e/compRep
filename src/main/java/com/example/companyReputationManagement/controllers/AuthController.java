package com.example.companyReputationManagement.controllers;

import com.example.companyReputationManagement.dto.user.dto.login.UserLoginRequestDTO;
import com.example.companyReputationManagement.dto.user.dto.login.UserLoginResponse;
import com.example.companyReputationManagement.dto.user.dto.login.UserLoginResponseDTO;
import com.example.companyReputationManagement.iservice.IUserService;
import com.example.companyReputationManagement.dto.StringResponseDto;
import com.example.companyReputationManagement.dto.user.dto.create.UserCreateRequestDTO;
import com.example.companyReputationManagement.dto.user.dto.create.UserCreateResponseDTO;
import com.example.companyReputationManagement.httpResponse.HttpResponseBody;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import com.example.companyReputationManagement.dto.StringResponse;

@RequiredArgsConstructor
@CrossOrigin(maxAge = 3600)
@RestController
@RequestMapping("/auth")
public class AuthController {
    private final IUserService userService;

    @GetMapping(value = "/login")
    public HttpResponseBody<UserLoginResponseDTO> authenticate(@Valid @RequestBody UserLoginRequestDTO userLoginRequestDTO) {

        return userService.login(userLoginRequestDTO);
    }

    @PostMapping(value = "/signup")
    public HttpResponseBody<UserCreateResponseDTO> register(@Valid @RequestBody UserCreateRequestDTO userCreateRequestDTO) {
        return userService.register(userCreateRequestDTO);
    }
}
