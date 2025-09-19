package com.community.management.dto.response;

import com.community.management.entity.AccessLevel;
import com.community.management.entity.DocumentCategory;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class DocumentResponse {
    private UUID id;
    private String title;
    private String description;
    private DocumentCategory category;
    private AccessLevel accessLevel;
    private String fileType;
    private String fileSize;
    private String fileUrl;
    private UUID uploadedBy;
    private String uploadedByName;
    private Integer downloadCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
