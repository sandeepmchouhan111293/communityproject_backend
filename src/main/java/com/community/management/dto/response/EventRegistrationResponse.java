package com.community.management.dto.response;

import com.community.management.entity.RegistrationStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class EventRegistrationResponse {
    private UUID id;
    private UUID eventId;
    private String eventTitle;
    private UUID userId;
    private String userName;
    private RegistrationStatus status;
    private LocalDateTime registeredAt;
}
