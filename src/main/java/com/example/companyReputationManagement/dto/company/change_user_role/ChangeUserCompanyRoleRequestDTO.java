package com.example.companyReputationManagement.dto.company.change_user_role;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Data
@Getter
@Setter
public class ChangeUserCompanyRoleRequestDTO implements Serializable {
    @NotNull
    private String username;
    @NotNull
    @Min(0)
    @Max(2)
    private Long newRoleId;
    @NotNull
    private String companyCode;

    public ChangeUserCompanyRoleRequestDTO(String username, Long newRoleId, String companyCode) {
        this.username = username;
        this.newRoleId = newRoleId;
        this.companyCode = companyCode;
    }
}
