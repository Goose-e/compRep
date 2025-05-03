package com.example.companyReputationManagement.iservice;

import com.example.companyReputationManagement.dto.jwt.RefreshRequestDTO;
import com.example.companyReputationManagement.dto.jwt.RefreshResponseDTO;
import com.example.companyReputationManagement.httpResponse.HttpResponseBody;
import com.example.companyReputationManagement.models.enums.RoleEnum;
import org.springframework.security.oauth2.core.OAuth2AccessToken;

import java.util.List;

public interface IJwtService {
    String extractUserCodeFromJwt();

    List<OAuth2AccessToken> createTokens(String userCode, String username, RoleEnum role);

    HttpResponseBody<RefreshResponseDTO> refreshToken(RefreshRequestDTO refreshRequestDTO);


}
