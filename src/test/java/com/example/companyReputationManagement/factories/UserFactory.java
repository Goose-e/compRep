package com.example.companyReputationManagement.factories;

import com.example.companyReputationManagement.dto.user.create.UserCreateRequestDTO;
import com.example.companyReputationManagement.dto.user.edit.EditUserRequestDTO;
import com.example.companyReputationManagement.dto.user.edit.EditUserResponseDTO;
import com.example.companyReputationManagement.dto.user.get_by_code.GetUserByCodeResponseDTO;
import com.example.companyReputationManagement.models.CompanyUser;
import com.example.companyReputationManagement.models.enums.RoleEnum;

public class UserFactory {
    public static CompanyUser defaultUser() {
        CompanyUser user = new CompanyUser();
        user.setCoreEntityId(1L);
        user.setUsername("test");
        user.setEmail("test@test.com");
        user.setPasswordHash("test");
        user.setUserCode("code");
        user.setRoleRefId(RoleEnum.USER);
        return user;
    }

    public static UserCreateRequestDTO validCreateRequest() {
        return new UserCreateRequestDTO("test@example.com", "test", "test");
    }

    public static EditUserRequestDTO editRequest() {
        return new EditUserRequestDTO("new@example.com", "newUsername");
    }

    public static EditUserResponseDTO editResponse() {
        return new EditUserResponseDTO(
                "newUsername", "new@example.com"
        );
    }

    public static GetUserByCodeResponseDTO getUserResponse() {
        return new GetUserByCodeResponseDTO("test","test@test.com","code");
    }
}
