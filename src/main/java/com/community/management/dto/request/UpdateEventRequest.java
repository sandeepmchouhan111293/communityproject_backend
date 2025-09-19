package com.community.management.dto.request;

import com.community.management.entity.EventStatus;
import jakarta.validation.constraints.Future;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UpdateEventRequest {
    private String title;
    private String description;
    @Future
    private LocalDateTime eventDate;
    private LocalDateTime endDate;
    private String location;
    private Integer maxParticipants;
    private EventStatus status;
    private String imageUrl;
    private Boolean registrationRequired;
}
