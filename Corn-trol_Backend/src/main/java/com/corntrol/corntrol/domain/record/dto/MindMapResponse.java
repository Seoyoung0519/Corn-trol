package com.corntrol.corntrol.domain.record.dto;

import lombok.Builder;
import lombok.Getter;
import java.util.List;

@Getter
@Builder
public class MindMapResponse {
    private List<Node> nodes;
    private List<Link> links;

    @Getter @Builder
    public static class Node {
        private Long recordId;
        private String keyword;
    }

    @Getter @Builder
    public static class Link {
        private Long sourceId;
        private Long targetId;
        private String topic;
    }
}
