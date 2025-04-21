package com.example.companyReputationManagement.dto.company.change_user_role;

import com.example.companyReputationManagement.httpResponse.ResponseDto;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class ChangeUserRoleResponseDTO implements ResponseDto {
    private String username;
    private String role;

    public ChangeUserRoleResponseDTO(String username, String role) {
        this.username = username;
        this.role = role;
    }
}
