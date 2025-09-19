package com.community.management.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CreateVolunteerOpportunityRequest {
    @NotBlank
    private String title;
    private String description;
    private String requirements;
    private String location;
    @NotNull
    @Future
    private LocalDateTime dateTime;
    private Integer durationHours;
    private Integer maxVolunteers;
}
