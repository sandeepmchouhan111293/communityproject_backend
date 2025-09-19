package com.community.management.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateUserProfileRequest {

    @Size(max = 255)
    private String fullName;

    @Size(max = 255)
    private String city;

    @Size(max = 255)
    private String state;

    @Size(max = 255)
    private String district;

    @Size(max = 255)
    private String communityName;

    @Size(max = 20)
    private String phone;
}
