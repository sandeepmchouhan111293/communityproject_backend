package com.community.management.dto.request;

import com.community.management.entity.AccessLevel;
import com.community.management.entity.DocumentCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateDocumentRequest {
    @NotBlank
    private String title;
    private String description;
    @NotNull
    private DocumentCategory category;
    @NotNull
    private AccessLevel accessLevel;
}
