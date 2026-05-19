package com.corntrol.corntrol.domain.record.controller;

import com.corntrol.corntrol.domain.record.dto.MindMapResponse;
import com.corntrol.corntrol.domain.record.dto.RecordDto;
import com.corntrol.corntrol.domain.record.service.RecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;

@Tag(name = "기록", description = "알곡(기록) CRUD 및 검색 API")
@RestController
@RequestMapping("/records")
@RequiredArgsConstructor
public class RecordController {

    private final RecordService recordService;

    @Operation(summary = "기록 생성", description = "새로운 텍스트 또는 음성 기록을 생성합니다.")
    @PostMapping
    public ResponseEntity<Long> createRecord(@RequestBody RecordDto.CreateRequest request) {
        return ResponseEntity.ok(recordService.createRecord(request));
    }

    @Operation(summary = "기록 목록 조회", description = "특정 사용자의 기록을 페이징하여 조회합니다. date 파라미터 전송 시 해당 날짜의 기록만 필터링합니다.")
    @GetMapping
    public ResponseEntity<Page<RecordDto.Response>> getRecords(
            @RequestParam("userId") Long userId,
            @RequestParam(value = "date", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            Pageable pageable) {
        return ResponseEntity.ok(recordService.getRecords(userId, date, pageable));
    }

    @Operation(summary = "기록 상세 조회", description = "특정 기록의 상세 내용을 조회합니다.")
    @GetMapping("/{recordId}")
    public ResponseEntity<RecordDto.Response> getRecordDetail(@PathVariable("recordId") Long recordId) {
        return ResponseEntity.ok(recordService.getRecordDetail(recordId));
    }

    @Operation(summary = "기록 수정", description = "기존 기록의 내용을 수정합니다.")
    @PutMapping("/{recordId}")
    public ResponseEntity<Void> updateRecord(
            @PathVariable("recordId") Long recordId,
            @RequestBody RecordDto.UpdateRequest request) {
        recordService.updateRecord(recordId, request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "기록 삭제", description = "특정 기록을 삭제합니다.")
    @DeleteMapping("/{recordId}")
    public ResponseEntity<Void> deleteRecord(@PathVariable("recordId") Long recordId) {
        recordService.deleteRecord(recordId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "기록 검색", description = "키워드를 이용해 사용자의 기록을 검색합니다.")
    @GetMapping("/search")
    public ResponseEntity<Page<RecordDto.Response>> searchRecords(
            @RequestParam("userId") Long userId,
            @RequestParam("keyword") String keyword,
            Pageable pageable) {
        return ResponseEntity.ok(recordService.searchRecords(userId, keyword, pageable));
    }

    @Operation(summary = "마인드맵 전체 조회", description = "알곡 꿰기 화면을 위한 노드 및 링크 데이터 반환")
    @GetMapping("/mindmap")
    public ResponseEntity<MindMapResponse> getMindMap(@RequestParam("userId") Long userId) {
        return ResponseEntity.ok(recordService.getMindMap(userId));
    }
}