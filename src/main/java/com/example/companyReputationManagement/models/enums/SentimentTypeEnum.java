package com.example.companyReputationManagement.models.enums;

import lombok.Getter;

@Getter
public enum SentimentTypeEnum {
    POSITIVE(1L, "POSITIVE", "Positive"),
    NEUTRAL(2L, "NEUTRAL", "Neutral"),
    NEGATIVE(3L, "NEGATIVE", "Negative"),
    UNRATED(4L, "UNRATED", "Unrated");;

    private final Long id;
    private final String code;
    private final String Type;

    SentimentTypeEnum(Long id, String code, String Type) {
        this.id = id;
        this.code = code;
        this.Type = Type;
    }

    public static SentimentTypeEnum fromString(String code) {
        for (SentimentTypeEnum sentiment : SentimentTypeEnum.values()) {
            if (sentiment.getCode().equalsIgnoreCase(code)) {
                return sentiment;
            }
        }
        throw new IllegalArgumentException("Unknown sentiment type: " + code);
    }


    public static SentimentTypeEnum fromId(int id) {
        for (SentimentTypeEnum sentiment : SentimentTypeEnum.values()) {
            if (sentiment.getId() == id) {
                return sentiment;
            }
        }
        throw new IllegalArgumentException("Unknown sentiment id: " + id);
    }


    public static String toString(SentimentTypeEnum sentimentType) {
        return sentimentType != null ? sentimentType.getType() : null;
    }
}
