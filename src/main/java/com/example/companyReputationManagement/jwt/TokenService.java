package com.example.companyReputationManagement.jwt;

import com.example.companyReputationManagement.dao.UserDao;
import com.example.companyReputationManagement.dto.jwt.RefreshRequestDTO;
import com.example.companyReputationManagement.dto.jwt.RefreshResponse;
import com.example.companyReputationManagement.dto.jwt.RefreshResponseDTO;
import com.example.companyReputationManagement.httpResponse.HttpResponseBody;
import com.example.companyReputationManagement.mapper.JwtMapper;
import com.example.companyReputationManagement.models.CompanyUser;
import com.example.companyReputationManagement.models.enums.RoleEnum;

import lombok.AllArgsConstructor;
import org.springframework.security.oauth2.core.OAuth2AccessToken;

import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;


import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static com.example.companyReputationManagement.constants.SysConst.OC_BUGS;
import static com.example.companyReputationManagement.constants.SysConst.OC_OK;

@AllArgsConstructor
@Service
public class TokenService {
    private final JwtEncoder jwtEncoder;
    private final UserDao userDao;
    private final JwtDecoder jwtDecoder;
    private final JwtMapper jwtMapper;

    private boolean isRefreshTokenValid(Jwt jwt) {
        try {
            Instant expiresAt = jwt.getExpiresAt();
            String tokenType = jwt.getClaim("tokenType");
            return "refresh".equals(tokenType) && !expiresAt.isBefore(Instant.now());
        } catch (Exception e) {
            return false;
        }
    }

    public List<OAuth2AccessToken> createTokens(String userCode, String username, RoleEnum role) {
        Instant now = Instant.now();
        Instant accessTokenExpiresAt = now.plusSeconds(3600); // Access Token expires in 1 hour
        Instant refreshTokenExpiresAt = now.plusSeconds(86400); // Refresh Token expires in 1 day


        JwtClaimsSet accessTokenClaims = JwtClaimsSet.builder()
                .issuer("http://localhost:9000")
                .issuedAt(now)
                .expiresAt(accessTokenExpiresAt)
                .subject(username)
                .claim("userCode", userCode)            // Добавляем имя пользователя в claims
                .claim("role", role.name())
                .claim("tokenType", "access")
                .build();
        String accessToken = jwtEncoder.encode(JwtEncoderParameters.from(accessTokenClaims)).getTokenValue();


        JwtClaimsSet refreshTokenClaims = JwtClaimsSet.builder()
                .issuer("http://localhost:9000")
                .issuedAt(now)
                .expiresAt(refreshTokenExpiresAt)
                .subject(username)
                .claim("userCode", userCode)
                .claim("tokenType", "refresh")
                .build();
        String refreshToken = jwtEncoder.encode(JwtEncoderParameters.from(refreshTokenClaims)).getTokenValue();
        final List<OAuth2AccessToken> accessTokens = new ArrayList<>();
        accessTokens.add(new OAuth2AccessToken(OAuth2AccessToken.TokenType.BEARER, accessToken, now, accessTokenExpiresAt));
        accessTokens.add(new OAuth2AccessToken(OAuth2AccessToken.TokenType.BEARER, refreshToken, now, refreshTokenExpiresAt));

        return accessTokens;
    }

    private List<OAuth2AccessToken> generateNewAccessToken(String userCode, String username, RoleEnum role) {
        Instant now = Instant.now();
        Instant accessTokenExpiresAt = now.plusSeconds(3600); // Access Token expires in 1 hour
        Instant refreshTokenExpiresAt = now.plusSeconds(86400); // Refresh Token expires in 1 day

        JwtClaimsSet accessTokenClaims = JwtClaimsSet.builder()
                .issuer("http://localhost:9000")
                .issuedAt(now)
                .expiresAt(accessTokenExpiresAt)
                .subject(username)
                .claim("userCode", userCode+username)
                .claim("role", role.name())
                .claim("tokenType", "access")
                .build();
        String accessToken = jwtEncoder.encode(JwtEncoderParameters.from(accessTokenClaims)).getTokenValue();


        JwtClaimsSet refreshTokenClaims = JwtClaimsSet.builder()
                .issuer("http://localhost:9000")
                .issuedAt(now)
                .expiresAt(refreshTokenExpiresAt)
                .subject(username)
                .claim("userCode", userCode)
                .claim("tokenType", "refresh")
                .build();
        String newRefreshToken = jwtEncoder.encode(JwtEncoderParameters.from(refreshTokenClaims)).getTokenValue();
        final List<OAuth2AccessToken> accessTokens = new ArrayList<>();
        accessTokens.add(new OAuth2AccessToken(OAuth2AccessToken.TokenType.BEARER, accessToken, now, accessTokenExpiresAt));
        accessTokens.add(new OAuth2AccessToken(OAuth2AccessToken.TokenType.BEARER, newRefreshToken, now, refreshTokenExpiresAt));

        return accessTokens;
    }

    public HttpResponseBody<RefreshResponseDTO> refreshToken(RefreshRequestDTO refreshRequestDTO) {
        HttpResponseBody<RefreshResponseDTO> response = new RefreshResponse();
        try {
            Jwt jwt = jwtDecoder.decode(refreshRequestDTO.getRefreshToken());
            if (!isRefreshTokenValid(jwt)) {
                response.setMessage("Invalid refresh token");

            } else {
                String username = jwt.getClaim("username");
                CompanyUser user = userDao.findUserByUserName(username);
                List<OAuth2AccessToken> newAccessToken = generateNewAccessToken(user.getUserCode(), user.getUsername(), user.getRoleRefId());
                response.setMessage("Successfully refreshed token");

                response.setResponseEntity(jwtMapper.mapNewAccessTokenToRefreshResponseDTO(newAccessToken));
            }
            if (response.getErrors().isEmpty()) {
                response.setResponseCode(OC_OK);
            } else {
                response.setResponseCode(OC_BUGS);
            }

        } catch (JwtException e) {
            response.setMessage("Decode error");
            response.setError("JWT decode error");
            response.setResponseCode(OC_BUGS);

        }catch (Exception e) {
            response.setMessage("Unexpected error");
            response.setError("Unexpected error: " + e.getMessage());
            response.setResponseCode(OC_BUGS);
        }
        return response;
    }
}
