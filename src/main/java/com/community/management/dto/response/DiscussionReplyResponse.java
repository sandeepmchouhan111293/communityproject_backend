package com.community.management.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class DiscussionReplyResponse {
    private UUID id;
    private UUID discussionId;
    private String content;
    private UUID createdBy;
    private String createdByName;
    private UUID parentReplyId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
