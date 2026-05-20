package com.corntrol.corntrol.domain.media.controller;

import com.corntrol.corntrol.domain.media.dto.MediaUploadResponse;
import com.corntrol.corntrol.domain.media.entity.Media;
import com.corntrol.corntrol.domain.media.service.MediaService;
import com.corntrol.corntrol.domain.media.service.S3Service;
import com.corntrol.corntrol.domain.media.service.WhisperService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Tag(name = "Media", description = "미디어 파일 업로드 및 STT 처리 API")
@RestController
@RequestMapping("/media")
@RequiredArgsConstructor
public class MediaController {

    private final S3Service s3Service;
    private final WhisperService whisperService;
    private final MediaService mediaService;

    @Operation(summary = "파일 업로드 및 STT 변환")
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MediaUploadResponse> uploadMedia(@RequestPart("file") MultipartFile file) {
        String fileUrl = null;
        try {
            fileUrl = s3Service.uploadFile(file);
            String text = whisperService.transcribeAudio(file);

            Media savedMedia = mediaService.saveMedia(fileUrl, text);

            return ResponseEntity.ok(new MediaUploadResponse(
                    savedMedia.getId(),
                    savedMedia.getFileUrl(),
                    savedMedia.getTranscribedText()
            ));
        } catch (Exception e) {
            log.error("미디어 처리 중 예외 발생. 롤백을 시도합니다.", e);
            if (fileUrl != null) {
                try {
                    s3Service.deleteFile(fileUrl); // 에러 발생 시 S3 파일 삭제 (롤백)
                    log.info("S3 찌꺼기 파일 삭제 완료: {}", fileUrl);
                } catch (Exception deleteEx) {
                    log.error("S3 파일 삭제(롤백) 실패: {}", fileUrl, deleteEx);
                }
            }
            throw new RuntimeException("음성 처리 중 오류가 발생하여 업로드가 취소되었습니다.", e);
        }
    }

    @Operation(summary = "미디어 정보 조회")
    @GetMapping("/{id}")
    public ResponseEntity<MediaUploadResponse> getMedia(@PathVariable("id") Long id) {
        Media media = mediaService.getMedia(id);

        return ResponseEntity.ok(new MediaUploadResponse(
                media.getId(),
                media.getFileUrl(),
                media.getTranscribedText()
        ));
    }
}