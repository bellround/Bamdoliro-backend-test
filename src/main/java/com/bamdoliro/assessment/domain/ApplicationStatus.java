package com.bamdoliro.assessment.domain;

import java.util.EnumSet;
import java.util.Set;

public enum ApplicationStatus {
    DRAFT, // 초안 제출 안함
    SUBMITTED, // 제출함
    FIRST_PASSED, // 1차 합격자 발표에서 통과함
    REJECTED, // 제출 거부
    FINAL_PASSED; // 합격

    public boolean canTransitionTo(ApplicationStatus next) {
        return allowedNext().contains(next);
    }

    private Set<ApplicationStatus> allowedNext() {
        return switch (this) {
            case DRAFT -> EnumSet.of(SUBMITTED);
            case SUBMITTED -> EnumSet.of(FIRST_PASSED, REJECTED);
            case FIRST_PASSED -> EnumSet.of(FINAL_PASSED, REJECTED);
            case REJECTED -> EnumSet.of(SUBMITTED);
            case FINAL_PASSED -> EnumSet.noneOf(ApplicationStatus.class);
        };
    }
}
