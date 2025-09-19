package com.community.management.controller;

import com.community.management.dto.request.UpdateUserProfileRequest;
import com.community.management.dto.response.ApiResponse;
import com.community.management.dto.response.UserProfileResponse;
import com.community.management.security.UserPrincipal;
import com.community.management.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/profile")
    public ResponseEntity<UserProfileResponse> getCurrentUserProfile(@AuthenticationPrincipal UserPrincipal currentUser) {
        UserProfileResponse userProfile = userService.getUserProfile(currentUser.getId());
        return ResponseEntity.ok(userProfile);
    }

    @PutMapping("/profile")
    public ResponseEntity<UserProfileResponse> updateUserProfile(@AuthenticationPrincipal UserPrincipal currentUser,
                                                               @Valid @RequestBody UpdateUserProfileRequest updateUserProfileRequest) {
        UserProfileResponse updatedUserProfile = userService.updateUserProfile(currentUser.getId(), updateUserProfileRequest);
        return ResponseEntity.ok(updatedUserProfile);
    }

    @PostMapping("/avatar")
    public ResponseEntity<ApiResponse> uploadAvatar(@AuthenticationPrincipal UserPrincipal currentUser, @RequestParam("file") MultipartFile file) {
        String fileDownloadUri = userService.updateAvatar(currentUser.getId(), file);
        return ResponseEntity.ok(new ApiResponse(true, "Avatar updated successfully. You can download it from: " + fileDownloadUri));
    }
}