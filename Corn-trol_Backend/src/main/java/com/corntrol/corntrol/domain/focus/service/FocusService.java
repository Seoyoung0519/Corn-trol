package com.corntrol.corntrol.domain.focus.service;

import com.corntrol.corntrol.domain.focus.dto.AiApiDto;
import com.corntrol.corntrol.domain.focus.dto.QuestionResponse;
import com.corntrol.corntrol.domain.focus.entity.CoolingQuestion;
import com.corntrol.corntrol.domain.focus.entity.FocusSession;
import com.corntrol.corntrol.domain.focus.repository.CoolingQuestionRepository;
import com.corntrol.corntrol.domain.focus.repository.FocusSessionRepository;
import com.corntrol.corntrol.domain.record.entity.Record;
import com.corntrol.corntrol.domain.record.repository.RecordRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FocusService {

    private final RecordRepository recordRepository;
    private final WebClient webClient;
    private final CoolingQuestionRepository coolingQuestionRepository;
    private final FocusSessionRepository focusSessionRepository;

    @Transactional
    public void createFocusQuestions(Long userId, Long recordId, String topic) {
        Record current = recordRepository.findById(recordId)
                .orElseThrow(() -> new EntityNotFoundException("해당 기록을 찾을 수 없습니다."));

        List<Record> connectedRecords = recordRepository.findAllConnectedRecords(recordId);

        AiApiDto.Request request = AiApiDto.Request.builder()
                .userId(userId)
                .recordId(recordId)
                .topic(topic)
                .currentRecord(AiApiDto.RecordInfo.builder()
                        .content(current.getContent())
                        .keywords(parseKeywords(current.getKeywords()))
                        .build())
                .linkedRecords(connectedRecords.stream()
                        .map(r -> AiApiDto.LinkedRecordInfo.builder()
                                .recordId(r.getId())
                                .content(r.getContent())
                                .keywords(parseKeywords(r.getKeywords()))
                                .build())
                        .toList())
                .build();

        AiApiDto.Response response = webClient.post()
                .uri("https://corn-trol-ai-1.onrender.com/focus/questions")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(AiApiDto.Response.class)
                .block();

        if (response != null && response.getQuestions() != null) {
            List<CoolingQuestion> questions = response.getQuestions().stream()
                    .map(q -> CoolingQuestion.builder()
                            .recordId(recordId)
                            .userId(userId)
                            .topic(topic)
                            .questionText(q.getQuestionText())
                            .build())
                    .toList();
            coolingQuestionRepository.saveAll(questions);
        }
    }

    private List<String> parseKeywords(String keywords) {
        if (keywords == null || keywords.isBlank()) return List.of();
        return Arrays.stream(keywords.split(","))
                .map(String::trim)
                .toList();
    }

    public List<QuestionResponse> getQuestions(Long recordId) {
        return coolingQuestionRepository.findAllByRecordId(recordId).stream()
                .map(q -> new QuestionResponse(q.getId(), q.getQuestionText()))
                .toList();
    }

    // 집중 모드 시작 API 로직
    @Transactional
    public Long startFocus(Long userId, Long recordId, Integer duration) {
        FocusSession session = FocusSession.builder()
                .userId(userId)
                .recordId(recordId)
                .duration(duration)
                .startTime(java.time.LocalDateTime.now())
                .build();

        FocusSession savedSession = focusSessionRepository.save(session);
        return savedSession.getId();
    }

    // 집중 모드 종료 API 로직
    @Transactional
    public void endFocus(Long sessionId) {
        FocusSession session = focusSessionRepository.findById(sessionId)
                .orElseThrow(() -> new EntityNotFoundException("진행 중인 집중 세션을 찾을 수 없습니다."));

        session.endSession();
    }
}
