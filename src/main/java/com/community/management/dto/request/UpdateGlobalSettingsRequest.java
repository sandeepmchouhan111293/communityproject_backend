package com.community.management.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateGlobalSettingsRequest {
    @NotBlank
    private String settingKey;
    @NotBlank
    private String settingValue; // JSON string
}
