package com.example.companyReputationManagement.controllers;


import com.example.companyReputationManagement.dto.user.edit.EditUserRequestDTO;
import com.example.companyReputationManagement.dto.user.edit.EditUserResponseDTO;
import com.example.companyReputationManagement.dto.user.find.FindByNameRequestDTO;
import com.example.companyReputationManagement.dto.user.find.FindByNameResponseListDTO;
import com.example.companyReputationManagement.dto.user.get_by_code.GetUserByCodeResponseDTO;
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
    public HttpResponseBody<EditUserResponseDTO> editUser(@Valid @RequestBody EditUserRequestDTO editUserRequestDTO) {
        return iUserService.edit(editUserRequestDTO);
    }

    @GetMapping(value = "/get_by_code")
    public HttpResponseBody<GetUserByCodeResponseDTO> getUserByCode() {
        return iUserService.getUserByCode();
    }

    @PostMapping(value = "/find_by_username")
    public HttpResponseBody<FindByNameResponseListDTO> findByName(@Valid @RequestBody FindByNameRequestDTO findByNameRequestDTO) {
        return iUserService.findByName(findByNameRequestDTO);
    }
}
