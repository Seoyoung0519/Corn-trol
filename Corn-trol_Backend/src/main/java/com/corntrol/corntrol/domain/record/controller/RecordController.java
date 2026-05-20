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

import java.security.Principal;
import java.time.LocalDate;

@Tag(name = "기록", description = "알곡(기록) CRUD 및 검색 API")
@RestController
@RequestMapping("/records")
@RequiredArgsConstructor
public class RecordController {

    private final RecordService recordService;

    @Operation(summary = "기록 생성")
    @PostMapping
    public ResponseEntity<Long> createRecord(
            Principal principal,
            @RequestBody RecordDto.CreateRequest request) {
        return ResponseEntity.ok(recordService.createRecord(principal.getName(), request));
    }

    @Operation(summary = "기록 목록 조회")
    @GetMapping
    public ResponseEntity<Page<RecordDto.Response>> getRecords(
            Principal principal,
            @RequestParam(value = "date", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            Pageable pageable) {
        return ResponseEntity.ok(recordService.getRecords(principal.getName(), date, pageable));
    }

    @Operation(summary = "기록 상세 조회")
    @GetMapping("/{recordId}")
    public ResponseEntity<RecordDto.Response> getRecordDetail(
            @PathVariable("recordId") Long recordId,
            Principal principal) {
        return ResponseEntity.ok(recordService.getRecordDetail(principal.getName(), recordId));
    }

    @Operation(summary = "기록 수정")
    @PutMapping("/{recordId}")
    public ResponseEntity<Void> updateRecord(
            @PathVariable("recordId") Long recordId,
            Principal principal,
            @RequestBody RecordDto.UpdateRequest request) {
        recordService.updateRecord(principal.getName(), recordId, request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "기록 삭제")
    @DeleteMapping("/{recordId}")
    public ResponseEntity<Void> deleteRecord(
            @PathVariable("recordId") Long recordId,
            Principal principal) {
        recordService.deleteRecord(principal.getName(), recordId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "기록 검색")
    @GetMapping("/search")
    public ResponseEntity<Page<RecordDto.Response>> searchRecords(
            Principal principal,
            @RequestParam("keyword") String keyword,
            Pageable pageable) {
        return ResponseEntity.ok(recordService.searchRecords(principal.getName(), keyword, pageable));
    }

    @Operation(summary = "마인드맵 전체 조회")
    @GetMapping("/mindmap")
    public ResponseEntity<MindMapResponse> getMindMap(Principal principal) {
        return ResponseEntity.ok(recordService.getMindMap(principal.getName()));
    }
}