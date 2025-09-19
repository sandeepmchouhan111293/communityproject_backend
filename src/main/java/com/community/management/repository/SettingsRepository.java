package com.community.management.repository;

import com.community.management.entity.Settings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SettingsRepository extends JpaRepository<Settings, UUID> {
    Optional<Settings> findByUserIdAndSettingKey(UUID userId, String settingKey);
    List<Settings> findByUserId(UUID userId);
    Optional<Settings> findByIsGlobalTrueAndSettingKey(String settingKey);
    List<Settings> findByIsGlobalTrue();
}
