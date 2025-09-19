package com.community.management.dto.request;

import lombok.Data;

@Data
public class UpdateDiscussionRequest {
    private String title;
    private String content;
    private String category;
    private Boolean isPinned;
    private Boolean isLocked;
}
