package com.community.management.dto.response;

import com.community.management.entity.RegistrationStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class VolunteerRegistrationResponse {
    private UUID id;
    private UUID opportunityId;
    private String opportunityTitle;
    private UUID userId;
    private String userName;
    private RegistrationStatus status;
    private String notes;
    private LocalDateTime registeredAt;
}
