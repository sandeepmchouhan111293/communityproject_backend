package com.community.management.controller;

import com.community.management.dto.request.UpdateGlobalSettingsRequest;
import com.community.management.dto.request.UpdateUserSettingsRequest;
import com.community.management.dto.response.GlobalSettingsResponse;
import com.community.management.dto.response.UserSettingsResponse;
import com.community.management.security.UserPrincipal;
import com.community.management.service.SettingsService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/settings")
public class SettingsController {

    @Autowired
    private SettingsService settingsService;

    @GetMapping
    public ResponseEntity<List<UserSettingsResponse>> getUserSettings(@AuthenticationPrincipal UserPrincipal currentUser) {
        List<UserSettingsResponse> settings = settingsService.getUserSettings(currentUser);
        return ResponseEntity.ok(settings);
    }

    @PutMapping
    public ResponseEntity<UserSettingsResponse> updateUserSettings(@Valid @RequestBody UpdateUserSettingsRequest request,
                                                                 @AuthenticationPrincipal UserPrincipal currentUser) {
        UserSettingsResponse updatedSettings = settingsService.updateUserSettings(request, currentUser);
        return ResponseEntity.ok(updatedSettings);
    }

    @GetMapping("/global")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<GlobalSettingsResponse>> getGlobalSettings() {
        List<GlobalSettingsResponse> settings = settingsService.getGlobalSettings();
        return ResponseEntity.ok(settings);
    }

    @PutMapping("/global")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GlobalSettingsResponse> updateGlobalSettings(@Valid @RequestBody UpdateGlobalSettingsRequest request) {
        GlobalSettingsResponse updatedSettings = settingsService.updateGlobalSettings(request);
        return ResponseEntity.ok(updatedSettings);
    }
}
