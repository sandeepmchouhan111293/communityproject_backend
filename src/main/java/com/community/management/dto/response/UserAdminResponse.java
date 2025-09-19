package com.community.management.dto.response;

import com.community.management.entity.UserRole;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class UserAdminResponse {
    private UUID id;
    private String email;
    private String fullName;
    private UserRole role;
    private String city;
    private String state;
    private String district;
    private String communityName;
    private String phone;
    private String avatarUrl;
    private Boolean isActive;
    private LocalDateTime lastLogin;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
