package com.corntrol.corntrol.domain.connection.service;

import com.corntrol.corntrol.domain.analysis.entity.Analysis;
import com.corntrol.corntrol.domain.analysis.repository.AnalysisRepository;
import com.corntrol.corntrol.domain.connection.dto.*;
import com.corntrol.corntrol.domain.connection.entity.RecordLink;
import com.corntrol.corntrol.domain.connection.repository.RecordLinkRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ConnectionService {

    private final AnalysisRepository analysisRepository;
    private final RecordLinkRepository recordLinkRepository;
    private final RestClient restClient;

    public ConnectionService(AnalysisRepository analysisRepository, RecordLinkRepository recordLinkRepository) {
        this.analysisRepository = analysisRepository;
        this.recordLinkRepository = recordLinkRepository;
        this.restClient = RestClient.builder()
                .baseUrl("https://corn-trol-ai.onrender.com")
                .build();
    }

    @Transactional
    public AiRecommendResponse recommendTarget(ConnectionRecommendRequest request) {

        Analysis sourceAnalysis = analysisRepository.findTopByRecordIdOrderByIdDesc(request.getRecordId())
                .orElseThrow(() -> new IllegalArgumentException("분석 데이터가 없습니다. 먼저 분석을 진행해주세요. ID: " + request.getRecordId()));

        String targetTopic = sourceAnalysis.getTopic();

        List<RecordLink> existingLinks = recordLinkRepository.findByTopicAndIsConnectedTrue(targetTopic);

        Set<Long> candidateIds = existingLinks.stream()
                .flatMap(link -> Stream.of(link.getSourceRecordId(), link.getTargetRecordId()))
                .filter(id -> !id.equals(sourceAnalysis.getRecordId()))
                .collect(Collectors.toSet());

        List<AiRecordInfo> candidateRecords = candidateIds.stream()
                .map(analysisRepository::findTopByRecordIdOrderByIdDesc)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(a -> new AiRecordInfo(a.getRecordId(), a.getTopic(), a.getKeywords(), a.getEmbedding()))
                .toList();

        AiRecordInfo sourceInfo = new AiRecordInfo(
                sourceAnalysis.getRecordId(),
                sourceAnalysis.getTopic(),
                sourceAnalysis.getKeywords(),
                sourceAnalysis.getEmbedding()
        );

        AiRecommendRequest aiRequest = new AiRecommendRequest(request.getUserId(), sourceInfo, candidateRecords);

        // AI 서버 통신
        AiRecommendResponse response = restClient.post()
                .uri("/connections/recommend")
                .body(aiRequest)
                .retrieve()
                .body(AiRecommendResponse.class);

        if (response != null && response.getTargetRecordId() != null) {
            RecordLink newLink = RecordLink.builder()
                    .userId(Long.valueOf(response.getUserId()))
                    .sourceRecordId(response.getSourceRecordId())
                    .targetRecordId(response.getTargetRecordId())
                    .topic(response.getTopic())
                    .similarityScore((double) response.getSimilarityScore())
                    .keywordScore((double) response.getKeywordScore())
                    .finalScore((double) response.getFinalScore())
                    .isConnected(false)
                    .build();

            recordLinkRepository.save(newLink);
        }

        return response;
    }

    @Transactional(readOnly = true)
    public RecordLink getRecommendResult(Long recordId) {
        return recordLinkRepository.findTopBySourceRecordIdAndIsConnectedFalseOrderByIdDesc(recordId)
                .orElseThrow(() -> new IllegalArgumentException("대기 중인 추천 결과가 없습니다."));
    }

    @Transactional
    public Long createConnection(ConnectionCreateRequest request) {
        RecordLink link = recordLinkRepository.findBySourceRecordIdAndTargetRecordId(
                        request.getSourceRecordId(), request.getTargetRecordId())
                .orElseThrow(() -> new IllegalArgumentException("해당 추천 이력이 존재하지 않습니다."));

        link.setConnected(true);
        return link.getId();
    }

    @Transactional(readOnly = true)
    public List<RecordLink> getConnections(Long recordId) {
        return recordLinkRepository.findAllConnectedByRecordId(recordId);
    }

    @Transactional
    public void deleteConnection(Long linkId) {
        if (!recordLinkRepository.existsById(linkId)) {
            throw new IllegalArgumentException("해당 연결이 존재하지 않습니다.");
        }
        recordLinkRepository.deleteById(linkId);
    }
}
