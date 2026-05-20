package com.corntrol.corntrol.domain.record.service;

import com.corntrol.corntrol.domain.connection.entity.RecordLink;
import com.corntrol.corntrol.domain.connection.repository.RecordLinkRepository;
import com.corntrol.corntrol.domain.record.dto.MindMapResponse;
import com.corntrol.corntrol.domain.record.dto.RecordDto;
import com.corntrol.corntrol.domain.record.entity.Record;
import com.corntrol.corntrol.domain.record.entity.RecordType;
import com.corntrol.corntrol.domain.record.repository.RecordRepository;
import com.corntrol.corntrol.domain.user.entity.User;
import com.corntrol.corntrol.domain.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RecordService {

    private final RecordRepository recordRepository;
    private final UserRepository userRepository;
    private final RecordLinkRepository recordLinkRepository;

    @Transactional
    public Long createRecord(String email, RecordDto.CreateRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("해당 유저를 찾을 수 없습니다."));

        Record record = Record.builder()
                .user(user)
                .type(RecordType.valueOf(request.getType().toUpperCase()))
                .content(request.getContent())
                .audioUrl(request.getAudioUrl())
                .build();

        return recordRepository.save(record).getId();
    }

    public Page<RecordDto.Response> getRecords(String email, java.time.LocalDate date, Pageable pageable) {
        Long userId = getUserIdFromEmail(email);

        if (date == null) {
            return recordRepository.findAllByUser_Id(userId, pageable)
                    .map(RecordDto.Response::from);
        }

        java.time.LocalDateTime start = date.atStartOfDay();
        java.time.LocalDateTime end = date.atTime(23, 59, 59, 999999999);

        return recordRepository.findAllByUser_IdAndCreatedAtBetween(userId, start, end, pageable)
                .map(RecordDto.Response::from);
    }

    public RecordDto.Response getRecordDetail(String email, Long recordId) {
        Long userId = getUserIdFromEmail(email);
        Record record = recordRepository.findByIdAndUser_Id(recordId, userId)
                .orElseThrow(() -> new EntityNotFoundException("기록을 찾을 수 없거나 접근 권한이 없습니다."));
        return RecordDto.Response.from(record);
    }

    @Transactional
    public void updateRecord(String email, Long recordId, RecordDto.UpdateRequest request) {
        Long userId = getUserIdFromEmail(email);
        Record record = recordRepository.findByIdAndUser_Id(recordId, userId)
                .orElseThrow(() -> new EntityNotFoundException("기록을 찾을 수 없거나 접근 권한이 없습니다."));
        record.updateContent(request.getContent());
    }

    @Transactional
    public void deleteRecord(String email, Long recordId) {
        Long userId = getUserIdFromEmail(email);
        Record record = recordRepository.findByIdAndUser_Id(recordId, userId)
                .orElseThrow(() -> new EntityNotFoundException("권한이 없습니다."));

        recordRepository.delete(record);
    }

    public Page<RecordDto.Response> searchRecords(String email, String keyword, Pageable pageable) {
        Long userId = getUserIdFromEmail(email);
        return recordRepository.searchRecords(userId, keyword, pageable)
                .map(RecordDto.Response::from);
    }

    public MindMapResponse getMindMap(String email) {
        Long userId = getUserIdFromEmail(email);
        List<Record> records = recordRepository.findByUser_Id(userId);

        List<Long> myRecordIds = records.stream()
                .map(Record::getId)
                .toList();

        List<RecordLink> links = recordLinkRepository.findByUserId(userId);

        List<MindMapResponse.Node> nodes = records.stream()
                .map(record -> MindMapResponse.Node.builder()
                        .recordId(record.getId())
                        .keyword(record.getMainTopic())
                        .build())
                .toList();

        List<MindMapResponse.Link> linkDtos = links.stream()
                .filter(link -> myRecordIds.contains(link.getSourceRecordId()) && myRecordIds.contains(link.getTargetRecordId()))
                .map(link -> MindMapResponse.Link.builder()
                        .sourceId(link.getSourceRecordId())
                        .targetId(link.getTargetRecordId())
                        .topic(link.getTopic())
                        .build())
                .toList();

        return MindMapResponse.builder()
                .nodes(nodes)
                .links(linkDtos)
                .build();
    }

    private Long getUserIdFromEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("해당 유저를 찾을 수 없습니다."))
                .getId();
    }
}
