package com.example.companyReputationManagement.dto.user.login;

import com.example.companyReputationManagement.httpResponse.ResponseDto;
import lombok.Data;
import org.springframework.security.oauth2.core.OAuth2AccessToken;

import java.util.ArrayList;
import java.util.List;

@Data
public class UserLoginResponseDTO implements ResponseDto {
    private String refreshToken;
    private String accessToken;
    private long accessExpiresIn;
    private long refreshExpiresIn;
    private String tokenType;

    public UserLoginResponseDTO(String accessToken, long accessExpiresIn, String refreshToken, long refreshExpiresIn) {
        this.accessToken = accessToken;
        this.accessExpiresIn = accessExpiresIn;
        this.refreshToken = refreshToken;
        this.refreshExpiresIn = refreshExpiresIn;
        this.tokenType = "Bearer";
    }
}
