package com.corntrol.corntrol.domain.analysis.service;

import com.corntrol.corntrol.domain.analysis.dto.AiAnalysisRequest;
import com.corntrol.corntrol.domain.analysis.dto.AiAnalysisResponse;
import com.corntrol.corntrol.domain.analysis.dto.AnalysisCreateRequest;
import com.corntrol.corntrol.domain.analysis.entity.Analysis;
import com.corntrol.corntrol.domain.analysis.repository.AnalysisRepository;
import com.corntrol.corntrol.domain.record.entity.Record;
import com.corntrol.corntrol.domain.record.repository.RecordRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;

@Service
public class AnalysisService {

    private final AnalysisRepository analysisRepository;
    private final RecordRepository recordRepository;
    private final RestClient restClient;

    public AnalysisService(AnalysisRepository analysisRepository, RecordRepository recordRepository) {
        this.analysisRepository = analysisRepository;
        this.recordRepository = recordRepository;
        this.restClient = RestClient.builder()
                .baseUrl("https://corntrol-ai-1.onrender.com")
                .build();
    }

    @Transactional
    public AiAnalysisResponse analyzeAndSave(AnalysisCreateRequest request) {
        Record record = recordRepository.findById(request.getRecordId())
                .orElseThrow(() -> new IllegalArgumentException("해당 기록을 찾을 수 없습니다. ID: " + request.getRecordId()));

        AiAnalysisRequest aiRequest = new AiAnalysisRequest(
                record.getId(),
                String.valueOf(record.getUser().getId()),
                record.getContent()
        );

        AiAnalysisResponse responseDto = restClient.post()
                .uri("/analysis")
                .body(aiRequest)
                .retrieve()
                .body(AiAnalysisResponse.class);

        if (responseDto != null) {
            Analysis analysis = Analysis.builder()
                    .recordId(responseDto.getRecordId())
                    .userId(responseDto.getUserId())
                    .content(responseDto.getContent())
                    .topic(responseDto.getTopic())
                    .keywords(responseDto.getKeywords())
                    .embedding(responseDto.getEmbedding())
                    .build();

            analysisRepository.save(analysis);

            String keywordsString = responseDto.getKeywords() != null ?
                    String.join(", ", responseDto.getKeywords()) : null;

            record.updateAnalysisResults(responseDto.getTopic(), keywordsString);
        }

        return responseDto;
    }

    @Transactional(readOnly = true)
    public AiAnalysisResponse getAnalysis(Long recordId) {
        Analysis analysis = analysisRepository.findTopByRecordIdOrderByIdDesc(recordId)
                .orElseThrow(() -> new IllegalArgumentException("해당 기록에 대한 분석 결과가 존재하지 않습니다. ID: " + recordId));

        return new AiAnalysisResponse(
                analysis.getRecordId(),
                analysis.getUserId(),
                analysis.getContent(),
                analysis.getTopic(),
                analysis.getKeywords(),
                analysis.getEmbedding()
        );
    }
}
