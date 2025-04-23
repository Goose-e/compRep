package com.example.companyReputationManagement.controllers;


import com.example.companyReputationManagement.dto.user.edit.EditUserRequestDTO;
import com.example.companyReputationManagement.dto.user.edit.EditUserResponseDTO;
import com.example.companyReputationManagement.httpResponse.HttpResponseBody;
import com.example.companyReputationManagement.iservice.IUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@CrossOrigin(maxAge = 3600)
@RestController
@RequestMapping("/user")
public class UserController {
    private final IUserService iUserService;

    @PatchMapping(value = "/edit")
    public HttpResponseBody<EditUserResponseDTO> createCompany(@Valid @RequestBody EditUserRequestDTO editUserRequestDTO) {
        return iUserService.edit(editUserRequestDTO);
    }


}
