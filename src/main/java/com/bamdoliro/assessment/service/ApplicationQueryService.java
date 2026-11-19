package com.bamdoliro.assessment.service;

import com.bamdoliro.assessment.domain.Application;
import com.bamdoliro.assessment.domain.ApplicationStatus;
import com.bamdoliro.assessment.domain.Track;
import com.bamdoliro.assessment.repository.InMemoryApplicantRepository;
import com.bamdoliro.assessment.repository.InMemoryApplicationRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

public class ApplicationQueryService {

    private final InMemoryApplicationRepository applicationRepository;
    private final InMemoryApplicantRepository applicantRepository;

    public ApplicationQueryService(InMemoryApplicationRepository applicationRepository,
                                   InMemoryApplicantRepository applicantRepository) {
        this.applicationRepository = applicationRepository;
        this.applicantRepository = applicantRepository;
    }

    /**
     * 관리자 지원자 목록. 전형/상태로 거를 수 있고(null 이면 전체), 접수 순(id 오름차순)으로 페이지네이션한다.
     * page 는 0부터 시작한다.
     */
    public List<ApplicationSummary> list(Track track, ApplicationStatus status, int page, int size) {
        List<Application> applications = applicationRepository.findAll().stream() // store에 저장된 모든 값 가져오기
                .filter(a -> track == null || a.getTrack() == track) // track이 존재하지 않고 입력받은 track이면 제외
                .filter(a -> status == null || a.getStatus() == status) // status가 존재하지 않고 입력받은 status면 제외
                .sorted(Comparator.comparing(Application::getId)) // id순으로 정렬
                .skip((long) page * size) // 현재 페이지에서 데이터만 보여주게 함
                .limit(size) // 페이지에 보여줄 100개 추출
                .toList(); // list로 만들어서 줌

        List<ApplicationSummary> result = new ArrayList<>(); // 배열 만듬
        for (Application application : applications) { // 한 페이지 안에 있는 데이터들 전부 추출
            String name = applicantRepository.findById(application.getApplicantId()) // 데이터를 하나씩 가져온다
                    .map(a -> a.name()) // 이름만
                    .orElse("(알 수 없음)"); // 없으면 알수없음으로 체움
            result.add(new ApplicationSummary(application.getId(), name, application.getTrack(), application.getStatus())); // result에 데이터를 하나씩 저장
        } // 위에서는 모든 값 가져오는데 여기는 왜 하나씩 가져옴?
        return result; // 페이지 데이터 다
    }
}
