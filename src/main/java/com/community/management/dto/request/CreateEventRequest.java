package com.community.management.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CreateEventRequest {
    @NotBlank
    private String title;
    private String description;
    @NotNull
    @Future
    private LocalDateTime eventDate;
    private LocalDateTime endDate;
    private String location;
    private Integer maxParticipants;
    private String imageUrl;
    private boolean registrationRequired;
}
