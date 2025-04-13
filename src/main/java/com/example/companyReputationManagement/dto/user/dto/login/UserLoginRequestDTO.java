package com.example.companyReputationManagement.dto.user.dto.login;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

@Data
public class UserLoginRequestDTO implements Serializable {
    @NotNull
    private String usernameOrEmail;
    @NotNull
    private String password;

    public UserLoginRequestDTO(String usernameOrEmail, String password) {
        this.usernameOrEmail = usernameOrEmail;
        this.password = password;
    }
}
