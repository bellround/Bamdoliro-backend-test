package com.bamdoliro.assessment.domain;

public enum Track {
    MEISTER_TALENT("M"), // 마이스터 전문가
    GENERAL("G"), // 일반
    SOCIAL_INTEGRATION("S"); // 사회 통합

    private final String code;

    Track(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
