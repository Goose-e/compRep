package com.example.companyReputationManagement.models.enums;

import lombok.Getter;

@Getter
public enum SentimentTypeRef {
    POSITIVE(1, "POSITIVE", "Positive"),
    NEUTRAL(2, "NEUTRAL", "Neutral"),
    NEGATIVE(3, "NEGATIVE", "Negative");
    private final int id;
    private final String code;
    private final String Type;

    SentimentTypeRef(int id, String code, String Type) {
        this.id = id;
        this.code = code;
        this.Type = Type;
    }

    public static SentimentTypeRef fromString(String code) {
        for (SentimentTypeRef sentiment : SentimentTypeRef.values()) {
            if (sentiment.getCode().equalsIgnoreCase(code)) {
                return sentiment;
            }
        }
        throw new IllegalArgumentException("Unknown sentiment type: " + code);
    }


    public static SentimentTypeRef fromId(int id) {
        for (SentimentTypeRef sentiment : SentimentTypeRef.values()) {
            if (sentiment.getId() == id) {
                return sentiment;
            }
        }
        throw new IllegalArgumentException("Unknown sentiment id: " + id);
    }


    public static String toString(SentimentTypeRef sentimentType) {
        return sentimentType != null ? sentimentType.getType() : null;
    }
}
