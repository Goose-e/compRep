package com.example.companyReputationManagement.controllers;


import com.example.companyReputationManagement.dto.user.edit.EditUserRequestDTO;
import com.example.companyReputationManagement.dto.user.edit.EditUserResponseDTO;
import com.example.companyReputationManagement.dto.user.get_by_code.GetUserByCodeResponseDTO;
import com.example.companyReputationManagement.httpResponse.HttpResponseBody;
import com.example.companyReputationManagement.iservice.IUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/user")
public class UserController {
    private final IUserService iUserService;

    @PatchMapping(value = "/edit")
    public HttpResponseBody<EditUserResponseDTO> editUser(@Valid @RequestBody EditUserRequestDTO editUserRequestDTO) {
        return iUserService.edit(editUserRequestDTO);
    }

    @GetMapping(value = "/get_by_code")
    public HttpResponseBody<GetUserByCodeResponseDTO> getUserByCode() {
        return iUserService.getUserByCode();
    }

}
