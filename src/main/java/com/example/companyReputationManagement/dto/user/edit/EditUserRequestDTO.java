package com.example.companyReputationManagement.dto.user.edit;

import jakarta.validation.constraints.Email;
import lombok.Data;

import java.io.Serializable;

@Data
public class EditUserRequestDTO implements Serializable {
    private String newUsername;
    @Email
    private String newEmail;
    public EditUserRequestDTO(String newEmail, String newUsername) {
        this.newEmail = newEmail;
        this.newUsername = newUsername;
    }


}
