package com.example.companyReputationManagement.controllers.jwt;

import com.example.companyReputationManagement.dto.jwt.RefreshRequestDTO;
import com.example.companyReputationManagement.dto.jwt.RefreshResponseDTO;
import com.example.companyReputationManagement.httpResponse.HttpResponseBody;
import com.example.companyReputationManagement.iservice.services.jwt.TokenService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
@RestController
public class TokenController {

    private final TokenService tokenService;


    @PostMapping("/oauth2/token")
    public HttpResponseBody<RefreshResponseDTO> refreshToken(@Valid @RequestBody RefreshRequestDTO refreshRequestDTO) {
        return tokenService.refreshToken(refreshRequestDTO);
    }

}

