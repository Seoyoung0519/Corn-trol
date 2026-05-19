package com.corntrol.corntrol.domain.media.controller;

import com.corntrol.corntrol.domain.media.dto.MediaUploadResponse;
import com.corntrol.corntrol.domain.media.entity.Media;
import com.corntrol.corntrol.domain.media.service.MediaService;
import com.corntrol.corntrol.domain.media.service.S3Service;
import com.corntrol.corntrol.domain.media.service.WhisperService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "Media", description = "미디어 파일 업로드 및 STT 처리 API")
@RestController
@RequestMapping("/media")
@RequiredArgsConstructor
public class MediaController {

    private final S3Service s3Service;
    private final WhisperService whisperService;
    private final MediaService mediaService;

    @Operation(summary = "파일 업로드 및 STT 변환", description = "MultipartFile을 S3에 저장하고 음성을 텍스트로 변환하여 데이터베이스에 저장합니다.")
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MediaUploadResponse> uploadMedia(@RequestPart("file") MultipartFile file) {
        String fileUrl = s3Service.uploadFile(file);
        String text = whisperService.transcribeAudio(file);

        Media savedMedia = mediaService.saveMedia(fileUrl, text);

        return ResponseEntity.ok(new MediaUploadResponse(
                savedMedia.getId(),
                savedMedia.getFileUrl(),
                savedMedia.getTranscribedText()
        ));
    }

    @Operation(summary = "미디어 정보 조회", description = "미디어 ID를 통해 저장된 S3 URL과 STT 텍스트를 조회합니다.")
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
