package com.lec.spring.base.service;

import java.io.IOException;
import java.util.Map;

public interface HbtiMatcher {
    /**
     * 사용자의 HBTI를 기반으로 상위 매칭 결과를 찾습니다.
     * @param userId 사용자 ID
     * @return 매칭 결과 (HBTI 타입, 매칭 점수, 상세 정보 포함)
     */
    Map<String, Object> findTopMatches(Long userId) throws IOException;
}
