package com.example.companyReputationManagement.dto.company.add_user;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class AddUserRequestDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    @NotNull
    private String username;
    @NotNull
    private String companyCode;

    public AddUserRequestDTO(String username, String companyCode) {
        this.username = username;
        this.companyCode = companyCode;
    }
}
