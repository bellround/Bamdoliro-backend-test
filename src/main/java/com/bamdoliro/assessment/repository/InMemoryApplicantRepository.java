package com.bamdoliro.assessment.repository;

import com.bamdoliro.assessment.domain.Applicant;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class InMemoryApplicantRepository {

    private final Map<Long, Applicant> store = new ConcurrentHashMap<>();
    private final AtomicLong sequence = new AtomicLong();
    private final AtomicInteger queryCount = new AtomicInteger();

    public Applicant save(String name) {
        queryCount.incrementAndGet(); // queryCount에 1을 더한다
        Applicant applicant = new Applicant(sequence.incrementAndGet(), name); // sequence에 1을 더한것과 이름을 넣는다
        store.put(applicant.id(), applicant); // 내 번호와 내 번호와 이름이 들어간걸 store에 넣는다
        // 저거말고 applicant넣으면 안돼?
        return applicant; // 내 번호와 이름 들어간거 저장
    }

    public Optional<Applicant> findById(long id) {
        queryCount.incrementAndGet();
        return Optional.ofNullable(store.get(id)); // store에서 하나 가져감
    }

    /** SELECT ... WHERE id IN (...) 에 해당한다. 쿼리는 1번만 나간다. */
    public List<Applicant> findAllByIds(Collection<Long> ids) { // 이거 왜 안씀?
        queryCount.incrementAndGet();
        List<Applicant> result = new ArrayList<>();
        for (Long id : ids) {
            Applicant applicant = store.get(id);
            if (applicant != null) {
                result.add(applicant);
            }
        }
        return result;
    }

    /**
     * querycount를 보낸다
     * @return
     */
    public int queryCount() {
        return queryCount.get(); // querycount를 보낸다
    }

    /**
     * querycount를 0으로 초기화
     */
    public void resetQueryCount() {
        queryCount.set(0);
    }
}
