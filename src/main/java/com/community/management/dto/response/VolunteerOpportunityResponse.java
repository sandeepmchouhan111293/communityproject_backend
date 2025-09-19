package com.community.management.dto.response;

import com.community.management.entity.VolunteerStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class VolunteerOpportunityResponse {
    private UUID id;
    private String title;
    private String description;
    private String requirements;
    private String location;
    private LocalDateTime dateTime;
    private Integer durationHours;
    private Integer maxVolunteers;
    private Integer currentVolunteers;
    private VolunteerStatus status;
    private UUID createdBy;
    private String createdByName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
