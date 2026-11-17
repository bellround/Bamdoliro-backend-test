package com.bamdoliro.assessment.domain;

import com.bamdoliro.assessment.common.BusinessException;
import com.bamdoliro.assessment.common.ErrorCode;

public class Application {

    private Long id;
    private final long applicantId;
    private Track track;
    private String photoKey;
    private ApplicationStatus status;
    private String applicantNumber;

    private Application(Long id, long applicantId, Track track, String photoKey,
                        ApplicationStatus status, String applicantNumber) {
        this.id = id; // 제출하는거 id
        this.applicantId = applicantId; // 제출하는 사람 id
        this.track = track; // 입학 전형
        this.photoKey = photoKey; // 아마도 지원자 사진 id
        this.status = status; // 제출한 지원서의 상태
        this.applicantNumber = applicantNumber; // 접수번호
    }

    public static Application create(long applicantId, Track track, String photoKey) {
        return new Application(null, applicantId, track, photoKey, ApplicationStatus.DRAFT, null);
    }

    /** 저장소가 값을 복사해서 주고받는 것을 흉내 내기 위한 복사 메서드. */
    public Application copy() {
        return new Application(id, applicantId, track, photoKey, status, applicantNumber);
    }

    public void assignId(long id) {
        this.id = id;
    }

    public void update(Track track, String photoKey) {
        this.track = track;
        this.photoKey = photoKey;
    }

    public void changeStatus(ApplicationStatus next) {
        this.status = next;
    }

    public void markSubmitted(String applicantNumber) {
        if (this.status != ApplicationStatus.DRAFT) { // 지금 상태가 제출을 했다면
            throw new BusinessException(ErrorCode.ALREADY_SUBMITTED); // 예외 만들기
        }
        this.applicantNumber = applicantNumber; // 접수번호 만들기
        this.status = ApplicationStatus.SUBMITTED; // 상태를 제출안함에서 제출함으로 하기
    }

    public Long getId() {
        return id;
    }

    public long getApplicantId() {
        return applicantId;
    }

    public Track getTrack() {
        return track;
    }

    public String getPhotoKey() {
        return photoKey;
    }

    public ApplicationStatus getStatus() {
        return status;
    }

    public String getApplicantNumber() {
        return applicantNumber;
    }
}
