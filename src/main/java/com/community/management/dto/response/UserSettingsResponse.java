package com.community.management.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class UserSettingsResponse {
    private UUID id;
    private UUID userId;
    private String settingKey;
    private String settingValue;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
