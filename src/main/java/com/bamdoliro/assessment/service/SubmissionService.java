package com.bamdoliro.assessment.service;

import com.bamdoliro.assessment.common.BusinessException;
import com.bamdoliro.assessment.common.ErrorCode;
import com.bamdoliro.assessment.domain.Application;
import com.bamdoliro.assessment.domain.ApplicationStatus;
import com.bamdoliro.assessment.repository.InMemoryApplicationRepository;
import com.bamdoliro.assessment.storage.PhotoStorage;

import java.util.logging.Level;
import java.util.logging.Logger;

public class SubmissionService {

    private static final Logger log = Logger.getLogger(SubmissionService.class.getName());

    private final InMemoryApplicationRepository repository;
    private final PhotoStorage photoStorage;
    private final ApplicantNumberIssuer numberIssuer; // 접수번호

    public SubmissionService(InMemoryApplicationRepository repository,
                             PhotoStorage photoStorage,
                             ApplicantNumberIssuer numberIssuer) {
        this.repository = repository;
        this.photoStorage = photoStorage;
        this.numberIssuer = numberIssuer;
    }

    /** 원서를 최종 제출한다. 제출이 끝나면 접수번호가 발급된다. */
    public SubmitResult submit(long applicationId) {
        Application application = repository.findById(applicationId)
                .orElseThrow(() -> new BusinessException(ErrorCode.APPLICATION_NOT_FOUND));
        // 찾는 id가 존재하는지 확인 후 존재하면 Application형식의 객체 반환 없으면 예외 생성

        if (application.getStatus() != ApplicationStatus.DRAFT) { // 원서의 상태가 제출 안한게 아니라면
            throw new BusinessException(ErrorCode.ALREADY_SUBMITTED); // 예외 생성
        }

        try {
            validateRequired(application); // 사진에 이상한거 없는지 확인
            String number = numberIssuer.issue(application.getTrack()); // 입학전형 뭘로 했는지 주고 접수번호 받아온다
            application.markSubmitted(number); // 접수했다고 말하기
            repository.save(application); // 접수한거 저장
            return new SubmitResult(application.getId(), number);
        } catch (Exception e) {
            log.log(Level.WARNING, "원서 제출 중 오류가 발생했습니다. id=" + applicationId, e);
            return new SubmitResult(application.getId(), null);
        }
    }

    private void validateRequired(Application application) {
        String photoKey = application.getPhotoKey(); // photokey를 얻는다
        if (photoKey == null || photoKey.isBlank()) { // 사진이 없거나 비어있으면
            throw new BusinessException(ErrorCode.MISSING_REQUIRED_FIELD); // 예외 던지기
        }
        if (!photoStorage.exists(photoKey)) { // photoKey가 존재하지 않는다면
            // photokey를 얻었는데 없는건 뭔소
            throw new BusinessException(ErrorCode.MISSING_REQUIRED_FIELD); // 예외 만들기
        }
    }
}
