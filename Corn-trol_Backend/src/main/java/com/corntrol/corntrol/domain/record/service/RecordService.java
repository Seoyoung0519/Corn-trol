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
    public Long createRecord(RecordDto.CreateRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("해당 유저를 찾을 수 없습니다."));

        Record record = Record.builder()
                .user(user)
                .type(RecordType.valueOf(request.getType().toUpperCase()))
                .content(request.getContent())
                .audioUrl(request.getAudioUrl())
                .build();

        return recordRepository.save(record).getId();
    }

    public Page<RecordDto.Response> getRecords(Long userId, java.time.LocalDate date, Pageable pageable) {
        if (date == null) {
            return recordRepository.findAllByUser_Id(userId, pageable)
                    .map(RecordDto.Response::from);
        }

        java.time.LocalDateTime start = date.atStartOfDay();
        java.time.LocalDateTime end = date.atTime(23, 59, 59, 999999999);

        return recordRepository.findAllByUser_IdAndCreatedAtBetween(userId, start, end, pageable)
                .map(RecordDto.Response::from);
    }

    public RecordDto.Response getRecordDetail(Long recordId) {
        Record record = recordRepository.findById(recordId)
                .orElseThrow(() -> new EntityNotFoundException("기록을 찾을 수 없습니다."));
        return RecordDto.Response.from(record);
    }

    @Transactional
    public void updateRecord(Long recordId, RecordDto.UpdateRequest request) {
        Record record = recordRepository.findById(recordId)
                .orElseThrow(() -> new EntityNotFoundException("기록을 찾을 수 없습니다."));
        record.updateContent(request.getContent());
    }

    @Transactional
    public void deleteRecord(Long recordId) {
        recordRepository.deleteById(recordId);
    }

    public Page<RecordDto.Response> searchRecords(Long userId, String keyword, Pageable pageable) {
        return recordRepository.searchRecords(userId, keyword, pageable)
                .map(RecordDto.Response::from);
    }

    public MindMapResponse getMindMap(Long userId) {
        List<Record> records = recordRepository.findByUser_Id(userId);
        List<RecordLink> links = recordLinkRepository.findByUserId(userId);

        List<MindMapResponse.Node> nodes = records.stream()
                .map(record -> MindMapResponse.Node.builder()
                        .recordId(record.getId())
                        .keyword(record.getMainTopic())
                        .build())
                .toList();

        List<MindMapResponse.Link> linkDtos = links.stream()
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
}
