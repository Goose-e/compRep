package com.example.companyReputationManagement.dto.user.create;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;


import java.io.Serializable;

@Data
public class UserCreateRequestDTO implements Serializable {
    @NotNull
    @Email
    private String email;
    @NotNull
    @Size(min = 3, max = 20)
    private String username;
    @NotNull
    @Size(min = 8, max = 20)
    private String password;

    public UserCreateRequestDTO(String email, String username, String password) {
        this.email = email;
        this.username = username;
        this.password = password;
    }

}
