package com.community.management.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class DirectoryEntryResponse {
    private UUID id;
    private UUID userId;
    private String userName;
    private String displayName;
    private String contactInfo;
    private String bio;
    private String skills;
    private String interests;
    private String socialLinks;
    private boolean isPublic;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
