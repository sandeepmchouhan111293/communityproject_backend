package com.community.management.service;

import com.community.management.dto.request.UpdateUserProfileRequest;
import com.community.management.dto.response.UserAdminResponse;
import com.community.management.dto.response.UserProfileResponse;
import com.community.management.entity.User;
import com.community.management.entity.UserRole;
import com.community.management.exception.ResourceNotFoundException;
import com.community.management.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    @Qualifier("localFileStorageService")
    private FileStorageService fileStorageService;

    @Cacheable(value = "userProfiles", key = "#userId")
    @Transactional(readOnly = true)
    public UserProfileResponse getUserProfile(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        return mapUserToProfileResponse(user);
    }

    @CachePut(value = "userProfiles", key = "#userId")
    @Transactional
    public UserProfileResponse updateUserProfile(UUID userId, UpdateUserProfileRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        user.setFullName(request.getFullName());
        user.setCity(request.getCity());
        user.setState(request.getState());
        user.setDistrict(request.getDistrict());
        user.setCommunityName(request.getCommunityName());
        user.setPhone(request.getPhone());

        User updatedUser = userRepository.save(user);

        return mapUserToProfileResponse(updatedUser);
    }

    @CachePut(value = "userProfiles", key = "#userId")
    @Transactional
    public String updateAvatar(UUID userId, MultipartFile file) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        if (user.getAvatarUrl() != null) {
            try {
                String oldFileName = user.getAvatarUrl().substring(user.getAvatarUrl().lastIndexOf('/') + 1);
                fileStorageService.deleteFile(oldFileName);
            } catch (Exception e) {
                System.err.println("Could not delete old avatar: " + e.getMessage());
            }
        }

        String fileName = fileStorageService.storeFile(file);

        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/files/")
                .path(fileName)
                .toUriString();

        user.setAvatarUrl(fileDownloadUri);
        userRepository.save(user);

        return fileDownloadUri;
    }

    // Admin specific methods
    @Transactional(readOnly = true)
    public List<UserAdminResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapUserToAdminResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public UserAdminResponse getUserById(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        return mapUserToAdminResponse(user);
    }

    @CacheEvict(value = "userProfiles", key = "#userId") // Evict from cache when role is updated
    @Transactional
    public UserAdminResponse updateUserRole(UUID userId, UserRole newRole) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        user.setRole(newRole);
        User updatedUser = userRepository.save(user);
        return mapUserToAdminResponse(updatedUser);
    }

    @CacheEvict(value = "userProfiles", key = "#userId") // Evict from cache when user is deleted
    @Transactional
    public void deleteUser(UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User", "id", userId);
        }
        userRepository.deleteById(userId);
    }

    @Transactional(readOnly = true)
    public long countTotalUsers() {
        return userRepository.count();
    }

    @Transactional(readOnly = true)
    public long countActiveUsers() {
        return userRepository.countByIsActiveTrue();
    }

    @Transactional(readOnly = true)
    public long countUsersByRole(UserRole role) {
        return userRepository.countByRole(role);
    }

    private UserProfileResponse mapUserToProfileResponse(User user) {
        return UserProfileResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .city(user.getCity())
                .state(user.getState())
                .district(user.getDistrict())
                .communityName(user.getCommunityName())
                .phone(user.getPhone())
                .avatarUrl(user.getAvatarUrl())
                .role(user.getRole())
                .createdAt(user.getCreatedAt())
                .build();
    }

    private UserAdminResponse mapUserToAdminResponse(User user) {
        return UserAdminResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole())
                .city(user.getCity())
                .state(user.getState())
                .district(user.getDistrict())
                .communityName(user.getCommunityName())
                .phone(user.getPhone())
                .avatarUrl(user.getAvatarUrl())
                .isActive(user.getIsActive())
                .lastLogin(user.getLastLogin())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
