package com.community.management.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class NotificationResponse {
    private UUID id;
    private UUID userId;
    private String message;
    private String type;
    private UUID relatedEntityId;
    private boolean isRead;
    private LocalDateTime createdAt;
}
