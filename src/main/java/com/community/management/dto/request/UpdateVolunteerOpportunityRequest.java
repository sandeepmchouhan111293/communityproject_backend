package com.community.management.dto.request;

import com.community.management.entity.VolunteerStatus;
import jakarta.validation.constraints.Future;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UpdateVolunteerOpportunityRequest {
    private String title;
    private String description;
    private String requirements;
    private String location;
    @Future
    private LocalDateTime dateTime;
    private Integer durationHours;
    private Integer maxVolunteers;
    private VolunteerStatus status;
}
