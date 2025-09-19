package com.community.management.dto.response;

import com.community.management.entity.EventStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class EventResponse {
    private UUID id;
    private String title;
    private String description;
    private LocalDateTime eventDate;
    private LocalDateTime endDate;
    private String location;
    private Integer maxParticipants;
    private Integer currentParticipants;
    private EventStatus status;
    private UUID createdBy;
    private String createdByName;
    private String imageUrl;
    private boolean registrationRequired;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
