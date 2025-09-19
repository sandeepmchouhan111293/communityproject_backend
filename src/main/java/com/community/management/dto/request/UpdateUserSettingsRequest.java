package com.community.management.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateUserSettingsRequest {
    @NotBlank
    private String settingKey;
    @NotBlank
    private String settingValue; // JSON string
}
