package com.corntrol.corntrol.domain.media.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.util.Collections;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class WhisperService {

    @Value("${huggingface.api-key}")
    private String apiKey;

    @Value("${huggingface.url}")
    private String apiUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    public String transcribeAudio(MultipartFile audioFile) {
        File tempInput = null;
        File tempOutput = null;

        try {
            tempInput = File.createTempFile("input_", ".wav");
            audioFile.transferTo(tempInput);
            tempOutput = File.createTempFile("output_", ".mp3");

            ProcessBuilder pb = new ProcessBuilder(
                    "ffmpeg", "-y",
                    "-i", tempInput.getAbsolutePath(),
                    "-vn",
                    "-ar", "16000",
                    "-ac", "1",
                    "-b:a", "64k",
                    tempOutput.getAbsolutePath()
            );

            Process process = pb.start();
            int exitCode = process.waitFor();

            if (exitCode != 0) {
                throw new RuntimeException("오디오 변환에 실패했습니다. exit code: " + exitCode);
            }

            byte[] mp3Bytes = Files.readAllBytes(tempOutput.toPath());

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(apiKey);
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.setContentType(MediaType.valueOf("audio/mpeg"));

            HttpEntity<byte[]> requestEntity = new HttpEntity<>(mp3Bytes, headers);

            ResponseEntity<Map> response = restTemplate.exchange(
                    apiUrl,
                    HttpMethod.POST,
                    requestEntity,
                    Map.class
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return (String) response.getBody().get("text");
            } else {
                throw new RuntimeException("음성 인식에 실패했습니다.");
            }
        } catch (Exception e) {
            throw new RuntimeException("음성 인식 서비스와 통신할 수 없습니다.", e);
        } finally {
            if (tempInput != null && tempInput.exists()) {
                boolean isInputDeleted = tempInput.delete();
                if (!isInputDeleted) {
                    log.warn("임시 파일(Input) 삭제 실패. 수동 확인이 필요합니다: {}", tempInput.getAbsolutePath());
                }
            }
            if (tempOutput != null && tempOutput.exists()) {
                boolean isOutputDeleted = tempOutput.delete();
                if (!isOutputDeleted) {
                    log.warn("임시 파일(Output) 삭제 실패. 수동 확인이 필요합니다: {}", tempOutput.getAbsolutePath());
                }
            }
        }
    }
}