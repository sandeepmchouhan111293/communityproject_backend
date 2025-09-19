package com.community.management.service;

import com.community.management.dto.request.UpdateGlobalSettingsRequest;
import com.community.management.dto.request.UpdateUserSettingsRequest;
import com.community.management.dto.response.GlobalSettingsResponse;
import com.community.management.dto.response.UserSettingsResponse;
import com.community.management.entity.Settings;
import com.community.management.entity.User;
import com.community.management.exception.ResourceNotFoundException;
import com.community.management.exception.ValidationException;
import com.community.management.repository.SettingsRepository;
import com.community.management.repository.UserRepository;
import com.community.management.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class SettingsService {

    @Autowired
    private SettingsRepository settingsRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<UserSettingsResponse> getUserSettings(UserPrincipal currentUser) {
        return settingsRepository.findByUserId(currentUser.getId()).stream()
                .map(this::mapUserSettingsToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public UserSettingsResponse updateUserSettings(UpdateUserSettingsRequest request, UserPrincipal currentUser) {
        User user = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", currentUser.getId()));

        Settings settings = settingsRepository.findByUserIdAndSettingKey(currentUser.getId(), request.getSettingKey())
                .orElseGet(() -> {
                    Settings newSettings = new Settings();
                    newSettings.setUser(user);
                    newSettings.setSettingKey(request.getSettingKey());
                    newSettings.setGlobal(false);
                    return newSettings;
                });

        settings.setSettingValue(request.getSettingValue());
        Settings savedSettings = settingsRepository.save(settings);
        return mapUserSettingsToResponse(savedSettings);
    }

    @Transactional(readOnly = true)
    public List<GlobalSettingsResponse> getGlobalSettings() {
        return settingsRepository.findByIsGlobalTrue().stream()
                .map(this::mapGlobalSettingsToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public GlobalSettingsResponse updateGlobalSettings(UpdateGlobalSettingsRequest request) {
        Settings settings = settingsRepository.findByIsGlobalTrueAndSettingKey(request.getSettingKey())
                .orElseGet(() -> {
                    Settings newSettings = new Settings();
                    newSettings.setSettingKey(request.getSettingKey());
                    newSettings.setGlobal(true);
                    return newSettings;
                });

        settings.setSettingValue(request.getSettingValue());
        Settings savedSettings = settingsRepository.save(settings);
        return mapGlobalSettingsToResponse(savedSettings);
    }

    private UserSettingsResponse mapUserSettingsToResponse(Settings settings) {
        return UserSettingsResponse.builder()
                .id(settings.getId())
                .userId(settings.getUser() != null ? settings.getUser().getId() : null)
                .settingKey(settings.getSettingKey())
                .settingValue(settings.getSettingValue())
                .createdAt(settings.getCreatedAt())
                .updatedAt(settings.getUpdatedAt())
                .build();
    }

    private GlobalSettingsResponse mapGlobalSettingsToResponse(Settings settings) {
        return GlobalSettingsResponse.builder()
                .id(settings.getId())
                .settingKey(settings.getSettingKey())
                .settingValue(settings.getSettingValue())
                .createdAt(settings.getCreatedAt())
                .updatedAt(settings.getUpdatedAt())
                .build();
    }
}
