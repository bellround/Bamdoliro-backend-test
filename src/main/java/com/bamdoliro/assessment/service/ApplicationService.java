package com.bamdoliro.assessment.service;

import com.bamdoliro.assessment.common.BusinessException;
import com.bamdoliro.assessment.common.ErrorCode;
import com.bamdoliro.assessment.domain.Application;
import com.bamdoliro.assessment.domain.ApplicationStatus;
import com.bamdoliro.assessment.domain.Track;
import com.bamdoliro.assessment.repository.InMemoryApplicationRepository;

public class ApplicationService {

    private final InMemoryApplicationRepository repository;

    public ApplicationService(InMemoryApplicationRepository repository) {
        this.repository = repository;
    }

    public Application createDraft(long applicantId, Track track, String photoKey) {
        return repository.save(Application.create(applicantId, track, photoKey));
    }

    /**
     * 제출한 상태가 아닐때 제출물의 전형과 사진을 업데이트해준다
     * @param applicationId 업데이트할 제출물의 ID번호
     * @param track 업데이트 할려는 거
     * @param photoKey 업데이트 할려는 거
     * @return 제출물의 복사
     */
    public Application update(long applicationId, Track track, String photoKey) {
        Application application = find(applicationId);
        if (application.getStatus() != ApplicationStatus.DRAFT)
        {
            throw new BusinessException(ErrorCode.NOT_EDITABLE);
        }
        application.update(track, photoKey);
        return repository.save(application);
    }

    /** 관리자용. 선발 결과 반영 등에 쓰인다. */
    public Application changeStatus(long applicationId, ApplicationStatus next) {
        Application application = find(applicationId);
        if (application.getStatus() == ApplicationStatus.DRAFT && next == ApplicationStatus.SUBMITTED)
        {
            application.changeStatus(next); // DRAFT상태에서만 SUBMITTED로 바꿀 수 있음
            return repository.save(application);
        } else if ((application.getStatus() == ApplicationStatus.SUBMITTED || application.getStatus() != ApplicationStatus.FIRST_PASSED) && next == ApplicationStatus.REJECTED) {
            application.changeStatus(next); // SUBMITTED거나 FIRST_PASSED상태에서만 REJECTED로 바꿀 수 있음
            return repository.save(application);
        } else if (application.getStatus() == ApplicationStatus.SUBMITTED && next == ApplicationStatus.FIRST_PASSED) {
            application.changeStatus(next); // SUBMITTED상태에서만 FIRST_PASSED로 바꿀 수 있음
            return repository.save(application);
        } else if (application.getStatus() == ApplicationStatus.FIRST_PASSED && next == ApplicationStatus.FINAL_PASSED) {
            application.changeStatus(next); // FIRST_PASSED상태에서만 FINAL_PASSED로 바꿀 수 있음
            return repository.save(application);
        }
        throw new BusinessException(ErrorCode.INVALID_STATUS_TRANSITION); // 아니면 예물
    }

    private Application find(long applicationId) {
        return repository.findById(applicationId)
                .orElseThrow(() -> new BusinessException(ErrorCode.APPLICATION_NOT_FOUND));
    }
}
