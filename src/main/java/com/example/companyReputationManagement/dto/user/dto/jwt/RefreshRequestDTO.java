package com.example.companyReputationManagement.dto.user.dto.jwt;

import lombok.Data;

import java.io.Serializable;

@Data
public class RefreshRequestDTO implements Serializable {
    private String refreshToken;
    public RefreshRequestDTO(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
