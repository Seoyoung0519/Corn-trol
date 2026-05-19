package com.corntrol.corntrol.domain.media.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class MediaUploadResponse {
    private Long id;
    private String url;
    private String text;
}