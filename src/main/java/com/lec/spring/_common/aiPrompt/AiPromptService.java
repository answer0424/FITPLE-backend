package com.lec.spring._common.aiPrompt;

import com.lec.spring.base.domain.HBTI;
import com.lec.spring.base.repository.HbtiRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@Service
@RequiredArgsConstructor
public class AiPromptService {

    private final HbtiRepository hbtiRepository;

    public String createAiPrompt(Long userId) throws IOException, InterruptedException {
        // 파이썬 스크립트 경로와 인자로 전달할 input_text 준비
        String pythonScriptPath = "/path/to/generate_text.py";  // 파이썬 스크립트 경로

        HBTI myHBTI = hbtiRepository.findByUserId(userId).orElse(null);

        String inputText = myHBTI.getHbti();

        // ProcessBuilder로 파이썬 스크립트 실행
        ProcessBuilder processBuilder = new ProcessBuilder("python3", pythonScriptPath, inputText);
        processBuilder.redirectErrorStream(true);  // 표준 오류와 출력을 함께 처리

        // 프로세스 실행
        Process process = processBuilder.start();

        // 출력 읽기
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        StringBuilder output = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            output.append(line).append("\n");
        }

        // 프로세스 종료 코드 확인
        int exitCode = process.waitFor();
        System.out.println("Python script executed with exit code: " + exitCode);

        // 출력된 결과
        System.out.println("Generated text: " + output.toString().trim());
        return output.toString().trim();
    }
}