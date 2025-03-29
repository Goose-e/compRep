package com.example.companyReputationManagement.models.enums;

import lombok.Getter;


@Getter
public enum RoleEnum {
    USER(0, "USER", "USER"),
    ADMIN(1, "ADMIN", "ADMIN");
    private final String role;
    private final int id;
    private final String code;

    RoleEnum(int id, String code, String role) {
        this.id = id;
        this.code = code;
        this.role = role;
    }

    public static RoleEnum fromString(String code) {
        for (RoleEnum roleEnum : RoleEnum.values()) {
            if (roleEnum.getCode().equalsIgnoreCase(code)) {
                return roleEnum;
            }
        }
        throw new IllegalArgumentException("Unknown sentiment type: " + code);
    }


    public static RoleEnum fromId(int id) {
        for (RoleEnum roleEnum : RoleEnum.values()) {
            if (roleEnum.getId() == id) {
                return roleEnum;
            }
        }
        throw new IllegalArgumentException("Unknown sentiment id: " + id);
    }


    public static String toString(RoleEnum roleEnum) {
        return roleEnum != null ? roleEnum.getRole() : null;
    }

}
