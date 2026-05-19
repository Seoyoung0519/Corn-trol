package com.corntrol.corntrol.domain.media.service;

import com.corntrol.corntrol.domain.media.entity.Media;
import com.corntrol.corntrol.domain.media.repository.MediaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MediaService {

    private final MediaRepository mediaRepository;

    @Transactional
    public Media saveMedia(String fileUrl, String transcribedText) {
        Media media = Media.builder()
                .fileUrl(fileUrl)
                .transcribedText(transcribedText)
                .build();

        return mediaRepository.save(media);
    }

    @Transactional(readOnly = true)
    public Media getMedia(Long id) {
        return mediaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("해당 미디어를 찾을 수 없습니다."));
    }
}
