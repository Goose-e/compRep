package com.example.companyReputationManagement.mapper;

import com.example.companyReputationManagement.dto.user.dto.create.UserCreateRequestDTO;
import com.example.companyReputationManagement.dto.user.dto.create.UserCreateResponseDTO;
import com.example.companyReputationManagement.iservice.generate.GenerateCode;
import com.example.companyReputationManagement.models.CompanyUser;
import com.example.companyReputationManagement.models.enums.RoleEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

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
        companyUser.setCompanyId(null);
        companyUser.setUserCode(generateCode.generateCode(companyUser));
        return companyUser;
    }

    public UserCreateResponseDTO mapUserToUserDtoResponse(CompanyUser companyUser) {
        return new UserCreateResponseDTO(companyUser.getEmail(), companyUser.getUsername());
    }
}
