package com.lec.spring.base.service;

import java.io.IOException;
import java.util.Map;

public interface HbtiMatcher {
    Map<String, Object> findTopMatches(Long userId) throws IOException;
}
