package com.community.management.dto.request;

import lombok.Data;

@Data
public class UpdateDirectoryEntryRequest {
    private String displayName;
    private String contactInfo;
    private String bio;
    private String skills;
    private String interests;
    private String socialLinks;
    private Boolean isPublic;
}
