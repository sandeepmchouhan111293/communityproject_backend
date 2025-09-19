package com.community.management.dto.request;

import com.community.management.entity.AccessLevel;
import com.community.management.entity.DocumentCategory;
import lombok.Data;

@Data
public class UpdateDocumentRequest {
    private String title;
    private String description;
    private DocumentCategory category;
    private AccessLevel accessLevel;
}
