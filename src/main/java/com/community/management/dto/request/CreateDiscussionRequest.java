package com.community.management.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateDiscussionRequest {
    @NotBlank
    private String title;
    @NotBlank
    private String content;
    private String category;
}
