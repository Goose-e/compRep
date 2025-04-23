package com.example.companyReputationManagement.mapper;

import com.example.companyReputationManagement.dto.user.create.UserCreateRequestDTO;
import com.example.companyReputationManagement.dto.user.create.UserCreateResponseDTO;
import com.example.companyReputationManagement.dto.user.edit.EditUserRequestDTO;
import com.example.companyReputationManagement.dto.user.edit.EditUserResponseDTO;
import com.example.companyReputationManagement.dto.user.login.UserLoginResponseDTO;
import com.example.companyReputationManagement.iservice.generate.GenerateCode;
import com.example.companyReputationManagement.models.CompanyUser;
import com.example.companyReputationManagement.models.enums.RoleEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class UserMapper {


    private final PasswordEncoder passwordEncoder;
    private final GenerateCode generateCode;

    public CompanyUser mapUserDtoToUser(UserCreateRequestDTO userCreateRequestDTO) {
        CompanyUser companyUser = new CompanyUser();

        companyUser.setUsername(userCreateRequestDTO.getUsername());
        companyUser.setEmail(userCreateRequestDTO.getEmail());
        companyUser.setPasswordHash(passwordEncoder.encode(userCreateRequestDTO.getPassword()));
        companyUser.setRoleRefId(RoleEnum.USER);
        companyUser.setUserCode(generateCode.generateCode(companyUser));
        return companyUser;
    }

    public CompanyUser mapUserDtoToUser(EditUserRequestDTO editUserRequestDTO, CompanyUser companyUser) {
        companyUser.setUsername(editUserRequestDTO.getNewUsername() == null ? companyUser.getUsername() : editUserRequestDTO.getNewUsername());
        companyUser.setEmail(editUserRequestDTO.getNewEmail() == null ? companyUser.getEmail() : editUserRequestDTO.getNewEmail());
        return companyUser;
    }

    public EditUserResponseDTO mapUserToUserResponse(CompanyUser companyUser) {
        return new EditUserResponseDTO(
                companyUser.getUsername(), companyUser.getEmail()
        );
    }

    public UserCreateResponseDTO mapUserToUserDtoResponse(CompanyUser companyUser) {
        return new UserCreateResponseDTO(companyUser.getEmail(), companyUser.getUsername());
    }

    public UserLoginResponseDTO mapTokensToUserLoginResponseDTO(List<OAuth2AccessToken> tokens) {
        long timeAccess = tokens.getFirst().getExpiresAt().getEpochSecond() - tokens.getFirst().getIssuedAt().getEpochSecond();
        long timeRefresh = tokens.getLast().getExpiresAt().getEpochSecond() - tokens.getLast().getIssuedAt().getEpochSecond();

        return new UserLoginResponseDTO(tokens.getFirst().getTokenValue(), timeAccess, tokens.get(1).getTokenValue(), timeRefresh);
    }
}
