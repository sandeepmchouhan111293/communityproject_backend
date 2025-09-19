package com.community.management.dto.request;

import com.community.management.entity.RegistrationStatus;
import lombok.Data;

@Data
public class UpdateVolunteerRegistrationRequest {
    private RegistrationStatus status;
    private String notes;
}
