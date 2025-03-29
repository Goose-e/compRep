package com.example.companyReputationManagement.models.enums;

import lombok.Getter;

@Getter
public enum StatusEnum {
    ACTUAL(0, "ACTUAL", "Actual"),
    CLOSED(1, "CLOSED", "Closed"),
    NEW(2, "NEW", "New"),
    IN_PROGRESS(3, "IN_PROGRESS", "In Progress"),
    RESOLVED(4, "RESOLVED", "Resolved");
    final int id;
    final String status;
    final String code;

    StatusEnum(int id, String code, String status) {
        this.id = id;
        this.code = code;
        this.status = status;
    }

    public static StatusEnum fromCode(String code) {
        for (StatusEnum statusEnum : StatusEnum.values()) {
            if (statusEnum.getCode().equalsIgnoreCase(code)) {
                return statusEnum;
            }
        }
        throw new IllegalArgumentException("Unknown status code: " + code);
    }


    public static StatusEnum fromId(int id) {
        for (StatusEnum statusEnum : StatusEnum.values()) {
            if (statusEnum.getId() == id) {
                return statusEnum;
            }
        }
        throw new IllegalArgumentException("Unknown status id: " + id);
    }

    public static String toString(StatusEnum statusEnum) {
        return statusEnum != null ? statusEnum.getStatus() : null;
    }
}
