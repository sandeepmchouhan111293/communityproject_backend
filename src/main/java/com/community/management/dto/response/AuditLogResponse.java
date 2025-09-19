package com.community.management.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class AuditLogResponse {
    private UUID id;
    private UUID userId;
    private String userName;
    private String action;
    private String entityType;
    private UUID entityId;
    private String oldValues;
    private String newValues;
    private String ipAddress;
    private String userAgent;
    private LocalDateTime createdAt;
}
