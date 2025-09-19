package com.community.management.dto.request;

import com.community.management.entity.UserRole;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateUserRoleRequest {
    @NotNull
    private UserRole role;
}
