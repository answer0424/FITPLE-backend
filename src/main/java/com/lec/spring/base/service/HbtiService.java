package com.lec.spring.base.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lec.spring.base.domain.HBTI;
import com.lec.spring.base.domain.User;
import com.lec.spring.base.repository.HbtiRepository;
import com.lec.spring.base.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class HbtiService {

    private final HbtiRepository hbtiRepository;
    private final UserRepository userRepository;
    private final Path jsonFilePath;

    /**
     * JSON 데이터 로드
     * @return 전체 HBTI JSON 데이터 반환
     * @throws IOException JSON 파일 로드 실패 시 예외 발생
     */
    private Map<String, Object> loadHbtiData() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        String jsonContent = Files.readString(jsonFilePath);
        return mapper.readValue(jsonContent, new TypeReference<Map<String, Object>>() {});
    }

    /**
     * 답변을 기반으로 각 성향의 퍼센트를 계산
     * @param answers 사용자가 응답한 답변 리스트 (12개)
     * @return 각 성향의 퍼센트 맵 (M/B, E/I, C/N, P/G)
     */
    public Map<String, Double> calculateHbtiPercentages(List<Integer> answers) {
        System.out.println("calculateHbtiPercentages 진입");
        if (answers.size() != 12) {
            throw new IllegalArgumentException("12개의 답변이 필요합니다.");
        }

        // 각 성향별 점수 계산
        List<Integer> mScores = answers.subList(0, 3);
        List<Integer> eScores = answers.subList(3, 6);
        List<Integer> cScores = answers.subList(6, 9);
        List<Integer> pScores = answers.subList(9, 12);

        double mPercent = roundToOneDecimalPlace(calculateAverage(mScores));
        double ePercent = roundToOneDecimalPlace(calculateAverage(eScores));
        double cPercent = roundToOneDecimalPlace(calculateAverage(cScores));
        double pPercent = roundToOneDecimalPlace(calculateAverage(pScores));

        double bPercent = roundToOneDecimalPlace(100 - mPercent);
        double iPercent = roundToOneDecimalPlace(100 - ePercent);
        double nPercent = roundToOneDecimalPlace(100 - cPercent);
        double gPercent = roundToOneDecimalPlace(100 - pPercent);

        // 보정 로직: 합계 조정
        bPercent = adjustComplementaryValue(mPercent, bPercent);
        iPercent = adjustComplementaryValue(ePercent, iPercent);
        nPercent = adjustComplementaryValue(cPercent, nPercent);
        gPercent = adjustComplementaryValue(pPercent, gPercent);

        // 결과 저장
        Map<String, Double> percentages = new HashMap<>();
        percentages.put("M", mPercent);
        percentages.put("B", bPercent);
        percentages.put("E", ePercent);
        percentages.put("I", iPercent);
        percentages.put("C", cPercent);
        percentages.put("N", nPercent);
        percentages.put("P", pPercent);
        percentages.put("G", gPercent);

        System.out.println("calculateHbtiPercentages 끝");
        return percentages;
    }


    /**
     * 퍼센트를 기반으로 HBTI 결과 결정
     * @param percentages 각 성향의 퍼센트 맵
     * @return HBTI 결과 문자열 (예: "MECP")
     */
    public String determineHbti(Map<String, Double> percentages) {
        StringBuilder hbti = new StringBuilder();
        hbti.append(percentages.get("M") >= percentages.get("B") ? "M" : "B");
        hbti.append(percentages.get("E") >= percentages.get("I") ? "E" : "I");
        hbti.append(percentages.get("C") >= percentages.get("N") ? "C" : "N");
        hbti.append(percentages.get("P") >= percentages.get("G") ? "P" : "G");
        System.out.println("determineHbti 끝");
        return hbti.toString();
    }

    /**
     * 사용자 ID와 답변을 기반으로 HBTI 계산 후 저장
     * @param userId 사용자 ID
     * @param answers 사용자가 응답한 답변 리스트
     */
    @Transactional
    public void processHbti(Long userId, List<Integer> answers) {
        System.out.println("서비스 진입");

        // 퍼센트 계산
        Map<String, Double> percentages = calculateHbtiPercentages(answers);

        // HBTI 결과 결정
        String hbti = determineHbti(percentages);

        // 유저 정보 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        System.out.println(userId + "유저 아이디");
        System.out.println(user.toString());

        // 소수점 1자리로 반올림
        double roundedMb = roundToOneDecimalPlace(percentages.get("M"));
        double roundedEi = roundToOneDecimalPlace(percentages.get("E"));
        double roundedCn = roundToOneDecimalPlace(percentages.get("C"));
        double roundedPg = roundToOneDecimalPlace(percentages.get("P"));

        // HBTI 엔티티 생성 또는 업데이트
        HBTI hbtiEntity = hbtiRepository.findById(userId).orElse(new HBTI());
        //hbtiEntity.setId(userId); // primary key 설정
        hbtiEntity.setUser(user);
        hbtiEntity.setMbScore(roundedMb); // 반올림된 값 저장
        hbtiEntity.setEiScore(roundedEi);
        hbtiEntity.setCnScore(roundedCn);
        hbtiEntity.setPgScore(roundedPg);
        hbtiEntity.setHbti(hbti);

        System.out.println("processHbti () 받아온 값 : " + hbtiEntity.toString());
        // 저장
        hbtiRepository.save(hbtiEntity);
    }

    // 소수점 1자리로 반올림
    private double roundToOneDecimalPlace(double value) {
        return BigDecimal.valueOf(value)
                .setScale(1, RoundingMode.HALF_UP)
                .doubleValue();
    }

    /**
     * 특정 사용자 ID로 HBTI 결과를 조회
     * @param userId 사용자 ID
     * @return HBTI 결과 객체
     */
    public HBTI getHbtiByUserId(Long userId) {
        return hbtiRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("HBTI 결과를 찾을 수 없습니다. userId: " + userId));
    }

    /**
     * 특정 HBTI 유형 데이터를 반환
     * @param hbtiType HBTI 유형 (예: "MECP")
     * @return HBTI 유형의 상세 데이터
     * @throws IOException JSON 데이터 로드 실패 시 예외 발생
     */
    public Map<String, Object> getHbtiDataByType(String hbtiType) throws IOException {
        Map<String, Object> hbtiData = loadHbtiData();

        if (!hbtiData.containsKey(hbtiType)) {
            throw new IllegalArgumentException("HBTI 유형이 잘못되었습니다: " + hbtiType);
        }

        return (Map<String, Object>) hbtiData.get(hbtiType);
    }

    /**
     * JSON 데이터 전체 반환
     * @return JSON 데이터 전체 맵
     * @throws IOException JSON 파일 로드 실패 시 예외 발생
     */
    public Map<String, Object> getAllHbtiData() throws IOException {
        return loadHbtiData();
    }

    /**
     * 특정 사용자 ID로 HBTI 결과와 상세 정보를 결합하여 반환
     * @param userId 사용자 ID
     * @return HBTI 결과, 퍼센트, 세부 정보를 포함한 맵
     * @throws IOException JSON 데이터 로드 실패 시 예외 발생
     */
    public Map<String, Object> getHbtiResultWithDetails(Long userId) throws IOException {
        HBTI hbti = getHbtiByUserId(userId);
        Map<String, Double> percentages = new HashMap<>();
        percentages.put("M", hbti.getMbScore());
        percentages.put("B", 100 - hbti.getMbScore());
        percentages.put("E", hbti.getEiScore());
        percentages.put("I", 100 - hbti.getEiScore());
        percentages.put("C", hbti.getCnScore());
        percentages.put("N", 100 - hbti.getCnScore());
        percentages.put("P", hbti.getPgScore());
        percentages.put("G", 100 - hbti.getPgScore());

        Map<String, Object> hbtiDetails = getHbtiDataByType(hbti.getHbti());

        Map<String, Object> result = new HashMap<>();
        result.put("hbtiType", hbti.getHbti());
        result.put("percentages", percentages);
        result.put("details", hbtiDetails);

        return result;
    }

    // 공통 유틸리티 메서드
    private double calculateAverage(List<Integer> scores) {
        return scores.stream().mapToDouble(Integer::doubleValue).average().orElse(0);
    }

    public Map<String, Object> getHbtiResultWithDetailsAndMatches(Long userId) throws IOException {
        // HBTI 결과 및 퍼센트 계산
        HBTI hbti = getHbtiByUserId(userId);
        Map<String, Double> percentages = Map.of(
                "M", hbti.getMbScore(),
                "B", 100 - hbti.getMbScore(),
                "E", hbti.getEiScore(),
                "I", 100 - hbti.getEiScore(),
                "C", hbti.getCnScore(),
                "N", 100 - hbti.getCnScore(),
                "P", hbti.getPgScore(),
                "G", 100 - hbti.getPgScore()
        );

        // JSON 데이터 로드 및 트레이너 HBTI와의 매칭 계산
        Map<String, Object> allHbtiData = loadHbtiData();
        List<Map.Entry<String, Integer>> matches = allHbtiData.entrySet().stream()
                .map(entry -> Map.entry(
                        entry.getKey(),
                        calculateMatchScore(percentages, entry.getKey())
                ))
                .sorted((a, b) -> b.getValue().compareTo(a.getValue())) // 점수 내림차순 정렬
                .limit(3) // 상위 3개의 HBTI만 선택
                .toList();

        // 매칭된 트레이너 정보 조합 (dogImage와 label만 포함)
        List<Map<String, Object>> topMatches = matches.stream()
                .map(match -> {
                    Map<String, Object> hbtiData = (Map<String, Object>) allHbtiData.get(match.getKey());
                    return Map.of(
                            "hbtiType", match.getKey(),
                            "score", match.getValue(),
                            "details", Map.of(
                                    "dogImage", hbtiData.get("dogImage"),
                                    "label", hbtiData.get("label")
                            )
                    );
                })
                .toList();

        // 현재 유저의 HBTI 상세 정보
        Map<String, Object> details = getHbtiDataByType(hbti.getHbti());

        // 결과 조합
        Map<String, Object> result = Map.of(
                "hbtiType", hbti.getHbti(),
                "percentages", percentages,
                "details", details,
                "topMatches", topMatches
        );

        return result;
    }

    private int calculateMatchScore(Map<String, Double> userDimensions, String trainerType) {
        // 트레이너의 각 차원을 점수로 변환
        Map<String, Integer> trainerDimensions = Map.of(
                "M_B", trainerType.contains("M") ? 100 : 0,
                "E_I", trainerType.contains("E") ? 100 : 0,
                "C_N", trainerType.contains("C") ? 100 : 0,
                "P_G", trainerType.contains("P") ? 100 : 0
        );

        // 각 차원별 일치도를 계산
        Map<String, Double> matchScores = Map.of(
                "M_B", 100 - Math.abs(userDimensions.get("M") - trainerDimensions.get("M_B")),
                "E_I", 100 - Math.abs(userDimensions.get("E") - trainerDimensions.get("E_I")),
                "C_N", 100 - Math.abs(userDimensions.get("C") - trainerDimensions.get("C_N")),
                "P_G", 100 - Math.abs(userDimensions.get("P") - trainerDimensions.get("P_G"))
        );

        // 전체 점수는 각 차원 점수의 평균
        return (int) Math.round(
                (matchScores.get("M_B") + matchScores.get("E_I") + matchScores.get("C_N") + matchScores.get("P_G")) / 4
        );
    }

    private double adjustComplementaryValue(double primary, double complementary) {
        double sum = primary + complementary;
        if (sum != 100.0) {
            double adjustment = 100.0 - sum;
            return roundToOneDecimalPlace(complementary + adjustment);
        }
        return complementary;
    }
} // end class



