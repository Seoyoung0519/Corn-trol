package com.corntrol.corntrol.domain.media.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class WhisperService {

    @Value("${huggingface.api-key}")
    private String apiKey;

    @Value("${huggingface.url}")
    private String apiUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    public String transcribeAudio(MultipartFile audioFile) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(apiKey);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        String contentType = audioFile.getContentType();
        if (contentType != null) {
            if (contentType.contains("m4a") || contentType.contains("x-m4a")) {
                headers.setContentType(MediaType.valueOf("audio/mp4"));
            } else {
                headers.setContentType(MediaType.valueOf(contentType));
            }
        } else {
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        }

        try {
            HttpEntity<byte[]> requestEntity = new HttpEntity<>(audioFile.getBytes(), headers);

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
        }
    }
}