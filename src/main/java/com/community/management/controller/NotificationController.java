package com.community.management.controller;

import com.community.management.dto.response.NotificationResponse;
import com.community.management.security.UserPrincipal;
import com.community.management.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @GetMapping
    public ResponseEntity<List<NotificationResponse>> getUserNotifications(@AuthenticationPrincipal UserPrincipal currentUser) {
        List<NotificationResponse> notifications = notificationService.getUserNotifications(currentUser);
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/unread")
    public ResponseEntity<List<NotificationResponse>> getUserUnreadNotifications(@AuthenticationPrincipal UserPrincipal currentUser) {
        List<NotificationResponse> notifications = notificationService.getUserUnreadNotifications(currentUser);
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/unread/count")
    public ResponseEntity<Long> countUnreadNotifications(@AuthenticationPrincipal UserPrincipal currentUser) {
        long count = notificationService.countUnreadNotifications(currentUser);
        return ResponseEntity.ok(count);
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<NotificationResponse> markNotificationAsRead(@PathVariable UUID id, @AuthenticationPrincipal UserPrincipal currentUser) {
        NotificationResponse notification = notificationService.markNotificationAsRead(id, currentUser);
        return ResponseEntity.ok(notification);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotification(@PathVariable UUID id, @AuthenticationPrincipal UserPrincipal currentUser) {
        notificationService.deleteNotification(id, currentUser);
        return ResponseEntity.noContent().build();
    }
}
