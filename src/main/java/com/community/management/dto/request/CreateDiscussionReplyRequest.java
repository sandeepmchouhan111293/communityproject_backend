package com.community.management.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.UUID;

@Data
public class CreateDiscussionReplyRequest {
    @NotBlank
    private String content;
    private UUID parentReplyId;
}
