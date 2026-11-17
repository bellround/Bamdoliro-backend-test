package com.bamdoliro.assessment.service;

import com.bamdoliro.assessment.domain.Track;

import java.util.concurrent.ConcurrentHashMap;

/** 전형별 접수번호를 발급한다. 형식은 전형 코드 1글자 + 4자리 일련번호 (예: M0001). */
public class ApplicantNumberIssuer {

    private final ConcurrentHashMap<Track, Integer> counters = new ConcurrentHashMap<>();

    public String issue(Track track) {
        int current = counters.getOrDefault(track, 0); // 전형코드 1글자 넣기
        simulateSequenceStoreLatency(); // 시간지연 근데 왜 씀?
        int next = current + 1; // 0번 없게 1하고 등록한 학생수
        counters.put(track, next); //counters에 내 접수번호 넣기
        return track.getCode() + String.format("%04d", next); // 내 접수번호 반환
    }

    private void simulateSequenceStoreLatency() {
        try {
            Thread.sleep(2);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
