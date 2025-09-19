package com.community.management.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class DiscussionResponse {
    private UUID id;
    private String title;
    private String content;
    private String category;
    private UUID createdBy;
    private String createdByName;
    private boolean isPinned;
    private boolean isLocked;
    private int viewCount;
    private int replyCount;
    private List<DiscussionReplyResponse> replies;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
