package com.corntrol.corntrol.domain.media.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3Service {

    private final S3Client s3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    /**
     * 파일을 S3에 업로드하고 해당 파일의 퍼블릭 접근 URL을 반환
     */
    public String uploadFile(MultipartFile file) {
        String fileName = createFileName(file.getOriginalFilename());

        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(fileName)
                    .contentType(file.getContentType())
                    .build();

            s3Client.putObject(putObjectRequest,
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

            return getPublicUrl(fileName);
        } catch (IOException e) {
            throw new RuntimeException("파일 업로드 오류가 발생했습니다.", e);
        }
    }

    private String createFileName(String originalFileName) {
        return UUID.randomUUID().toString().concat(getFileExtension(originalFileName));
    }

    private String getFileExtension(String fileName) {
        try {
            return fileName.substring(fileName.lastIndexOf("."));
        } catch (StringIndexOutOfBoundsException e) {
            throw new IllegalArgumentException("잘못된 파일 형식입니다.");
        }
    }

    private String getPublicUrl(String fileName) {
        return String.format("https://%s.s3.ap-northeast-2.amazonaws.com/%s", bucket, fileName);
    }
}