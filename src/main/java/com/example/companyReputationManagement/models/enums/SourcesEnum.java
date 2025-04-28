package com.example.companyReputationManagement.models.enums;

import lombok.Getter;

public enum SourcesEnum {

    OTZOVIK(1L, "Otzovik", "https://otzovik.by/"),
    GOOGLE_REVIEWS(2L, "Google Reviews", "https://google.com"),
    YELP(3L, "Yelp", "https://yelp.com");

    private Long id;
    private String Code;
    @Getter
    private String name;
    @Getter
    private String url;

    // Конструктор для инициализации значений
    SourcesEnum(Long id, String name, String url) {
        this.id = id;
        this.name = name;
        this.url = url;
    }

    // Геттер для id
    public Long getSourceCode() {
        return id;
    }

    // Метод для получения enum по id (если нужно)
    public static SourcesEnum fromId(Long id) {
        for (SourcesEnum source : SourcesEnum.values()) {
            if (source.getSourceCode().equals(id)) {
                return source;
            }
        }
        throw new IllegalArgumentException("Unknown id: " + id);
    }
}
