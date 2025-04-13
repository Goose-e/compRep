package com.example.companyReputationManagement.models.enums;

import lombok.Getter;


@Getter
public enum StatusEnum {
    ACTUAL(0L, "ACTUAL", "Actual"),
    CLOSED(1L, "CLOSED", "Closed"),
    NEW(2L, "NEW", "New"),
    IN_PROGRESS(3L, "IN_PROGRESS", "In Progress"),
    RESOLVED(4L, "RESOLVED", "Resolved");
    final Long id;
    final String status;
    final String code;

    StatusEnum(Long id, String code, String status) {
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
