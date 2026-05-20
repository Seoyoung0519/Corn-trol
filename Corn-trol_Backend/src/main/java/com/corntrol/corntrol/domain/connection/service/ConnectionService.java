package com.corntrol.corntrol.domain.connection.service;

import com.corntrol.corntrol.domain.analysis.entity.Analysis;
import com.corntrol.corntrol.domain.analysis.repository.AnalysisRepository;
import com.corntrol.corntrol.domain.connection.dto.*;
import com.corntrol.corntrol.domain.connection.entity.RecordLink;
import com.corntrol.corntrol.domain.connection.repository.RecordLinkRepository;
import com.corntrol.corntrol.domain.record.entity.Record;
import com.corntrol.corntrol.domain.record.repository.RecordRepository;
import com.corntrol.corntrol.domain.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
public class ConnectionService {

    private final AnalysisRepository analysisRepository;
    private final RecordLinkRepository recordLinkRepository;
    private final RecordRepository recordRepository;
    private final UserRepository userRepository;
    private final RestClient restClient;

    public ConnectionService(AnalysisRepository analysisRepository,
                             RecordLinkRepository recordLinkRepository,
                             RecordRepository recordRepository,
                             UserRepository userRepository) {
        this.analysisRepository = analysisRepository;
        this.recordLinkRepository = recordLinkRepository;
        this.recordRepository = recordRepository;
        this.userRepository = userRepository;
        this.restClient = RestClient.builder()
                .baseUrl("https://corn-trol-ai.onrender.com")
                .build();
    }

    private Long getUserIdFromEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("해당 유저를 찾을 수 없습니다."))
                .getId();
    }

    @Transactional
    public AiRecommendResponse recommendTarget(String email, Long recordId) {
        Long userId = getUserIdFromEmail(email);

        Analysis sourceAnalysis = analysisRepository.findTopByRecordIdOrderByIdDesc(recordId)
                .orElseThrow(() -> new IllegalArgumentException("분석 데이터가 없습니다. 먼저 분석을 진행해주세요. ID: " + recordId));

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

        if (candidateRecords.isEmpty()) {
            List<Analysis> sameTopicRecords = analysisRepository.findByTopicAndRecordIdNot(targetTopic, sourceAnalysis.getRecordId());

            for (Analysis target : sameTopicRecords) {
                Long targetId = target.getRecordId();
                Long sourceId = sourceAnalysis.getRecordId();

                Record targetRecord = recordRepository.findById(targetId).orElse(null);
                if (targetRecord == null || !targetRecord.getUser().getId().equals(userId)) {
                    log.warn("[보안 필터] 다른 유저의 기록(ID: {})과 자동 연결을 시도하여 차단했습니다.", targetId);
                    continue;
                }

                boolean isSourceLinked = recordLinkRepository.existsBySourceRecordIdOrTargetRecordIdAndIsConnectedTrue(sourceId, sourceId);
                boolean isTargetLinked = recordLinkRepository.existsBySourceRecordIdOrTargetRecordIdAndIsConnectedTrue(targetId, targetId);
                boolean isPairAlreadyLinked = recordLinkRepository.existsConnectedLinkBetween(sourceId, targetId);

                if (!isSourceLinked && !isTargetLinked && !isPairAlreadyLinked) {
                    RecordLink autoLink = RecordLink.builder()
                            .userId(userId)
                            .sourceRecordId(sourceId)
                            .targetRecordId(targetId)
                            .topic(targetTopic)
                            .similarityScore(1.0)
                            .keywordScore(1.0)
                            .finalScore(1.0)
                            .isConnected(true)
                            .build();

                    recordLinkRepository.save(autoLink);
                    return new AiRecommendResponse(userId.intValue(), sourceId, targetId, targetTopic, 1f, 1f, 1f);
                }
            }
        }

        AiRecordInfo sourceInfo = new AiRecordInfo(
                sourceAnalysis.getRecordId(),
                sourceAnalysis.getTopic(),
                sourceAnalysis.getKeywords(),
                sourceAnalysis.getEmbedding()
        );

        AiRecommendRequest aiRequest = new AiRecommendRequest(userId.intValue(), sourceInfo, candidateRecords);

        AiRecommendResponse response = restClient.post()
                .uri("/connections/recommend")
                .body(aiRequest)
                .retrieve()
                .body(AiRecommendResponse.class);

        if (response != null && response.getTargetRecordId() != null) {

            Record aiTargetRecord = recordRepository.findById(response.getTargetRecordId())
                    .orElseThrow(() -> new IllegalArgumentException("추천된 타겟 기록을 찾을 수 없습니다."));

            if (!aiTargetRecord.getUser().getId().equals(userId)) {
                log.error("[보안 경고] AI가 타 유저의 기록(ID: {})을 추천했습니다. 저장을 차단합니다.", response.getTargetRecordId());
                throw new IllegalStateException("잘못된 AI 추천입니다. 타 유저의 기록에는 연결할 수 없습니다.");
            }

            RecordLink newLink = RecordLink.builder()
                    .userId(userId)
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
    public RecordLink getRecommendResult(String email, Long recordId) {
        Long userId = getUserIdFromEmail(email);
        RecordLink link = recordLinkRepository.findTopBySourceRecordIdAndIsConnectedFalseOrderByIdDesc(recordId)
                .orElseThrow(() -> new IllegalArgumentException("대기 중인 추천 결과가 없습니다."));

        if (!link.getUserId().equals(userId)) {
            throw new EntityNotFoundException("접근 권한이 없습니다.");
        }
        return link;
    }

    @Transactional
    public Long createConnection(String email, ConnectionCreateRequest request) {
        Long userId = getUserIdFromEmail(email);

        RecordLink link = recordLinkRepository.findBySourceRecordIdAndTargetRecordId(
                        request.getSourceRecordId(), request.getTargetRecordId())
                .orElseThrow(() -> new IllegalArgumentException("해당 추천 이력이 존재하지 않습니다."));

        if (!link.getUserId().equals(userId)) {
            throw new EntityNotFoundException("접근 권한이 없습니다.");
        }

        link.setConnected(true);
        return link.getId();
    }

    @Transactional(readOnly = true)
    public List<RecordLink> getConnections(String email, Long recordId) {
        Long userId = getUserIdFromEmail(email);
        Record record = recordRepository.findById(recordId)
                .orElseThrow(() -> new EntityNotFoundException("기록을 찾을 수 없습니다."));

        if(!record.getUser().getId().equals(userId)){
            throw new EntityNotFoundException("접근 권한이 없습니다.");
        }
        return recordLinkRepository.findAllConnectedByRecordId(recordId);
    }

    @Transactional
    public void deleteConnection(String email, Long linkId) {
        Long userId = getUserIdFromEmail(email);
        RecordLink link = recordLinkRepository.findById(linkId)
                .orElseThrow(() -> new IllegalArgumentException("해당 연결이 존재하지 않습니다."));

        if (!link.getUserId().equals(userId)) {
            throw new EntityNotFoundException("권한이 없습니다.");
        }
        recordLinkRepository.deleteById(linkId);
    }
}
