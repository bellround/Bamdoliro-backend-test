package com.bamdoliro.assessment.problems;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SelectionAlgorithm {
    private static void quickSort(List<Candidate> a, int l, int r)
    {
        if (l >= r)
        {
            return;
        }

        Candidate mi = a.get(l);
        int le = l + 1;
        int ri = r;
        while (le <= ri)
        {
            while (le <= r && a.get(le).score() >= mi.score())
            {
                Candidate x = a.get(le);
                if (x.score() == mi.score() && x.applicantNumber().compareTo(mi.applicantNumber()) > 0) break;
                le++;
            }
            while (ri > l && a.get(ri).score() <= mi.score())
            {
                Candidate x = a.get(ri);
                if (x.score() == mi.score() && x.applicantNumber().compareTo(mi.applicantNumber()) < 0) break;
                ri--;
            }
            if (le > ri)
            {
                Collections.swap(a, ri, l);
            }
            else
            {
                Collections.swap(a, ri, le);
            }
        }

        quickSort(a, l, ri - 1);
        quickSort(a, ri + 1, r);
    }

    /**
     * 파트 3-1. 규칙은 README.md 의 "파트 3-1" 절을 참고한다.
     *
     * @param candidates       지원자 목록 (정렬되어 있지 않을 수 있고, 이 메서드가 수정하면 안 된다)
     * @param quota            선발 정원
     * @param otherRegionLimit 부산 외 지역 지원자를 합격시킬 수 있는 최대 인원
     */
    public static SelectionResult select(List<Candidate> candidates, int quota, int otherRegionLimit) {
        if (quota <= 0 || candidates.isEmpty())
        {
            return new SelectionResult(List.of(), candidates);
        }
        List<Candidate> a = new ArrayList<>(candidates);
        quickSort(a, 0, a.size() - 1);
        System.out.println(a);

        double b = a.get(Math.min(quota, a.size()) - 1).score();
        List<Candidate> t = new ArrayList<>();
        List<Candidate> f = new ArrayList<>();
        for (Candidate candidate : a)
        {
            if (t.size() >= quota)
            {
                if (candidate.score() == b)
                {
                    t.add(candidate);
                }
                else
                {
                    f.add(candidate);
                }
            }
            else if (candidate.busan() || otherRegionLimit > 0)
            {
                t.add(candidate);
                if (!candidate.busan())
                {
                    otherRegionLimit--;
                }
            }
            else
            {
                f.add(candidate);
            }
        }
        return new SelectionResult(t, f);
    }
}
