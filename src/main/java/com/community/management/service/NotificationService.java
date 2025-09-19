package com.community.management.service;

import com.community.management.dto.response.NotificationResponse;
import com.community.management.entity.Notification;
import com.community.management.entity.User;
import com.community.management.exception.ResourceNotFoundException;
import com.community.management.repository.NotificationRepository;
import com.community.management.repository.UserRepository;
import com.community.management.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public NotificationResponse createNotification(UUID userId, String message, String type, UUID relatedEntityId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        Notification notification = new Notification();
        notification.setUser(user);
        notification.setMessage(message);
        notification.setType(type);
        notification.setRelatedEntityId(relatedEntityId);
        notification.setRead(false);

        Notification savedNotification = notificationRepository.save(notification);
        return mapNotificationToResponse(savedNotification);
    }

    @Transactional(readOnly = true)
    public List<NotificationResponse> getUserNotifications(UserPrincipal currentUser) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(currentUser.getId()).stream()
                .map(this::mapNotificationToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<NotificationResponse> getUserUnreadNotifications(UserPrincipal currentUser) {
        return notificationRepository.findByUserIdAndIsReadFalseOrderByCreatedAtDesc(currentUser.getId()).stream()
                .map(this::mapNotificationToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public long countUnreadNotifications(UserPrincipal currentUser) {
        return notificationRepository.countByUserIdAndIsReadFalse(currentUser.getId());
    }

    @Transactional
    public NotificationResponse markNotificationAsRead(UUID notificationId, UserPrincipal currentUser) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification", "id", notificationId));

        if (!notification.getUser().getId().equals(currentUser.getId())) {
            throw new ResourceNotFoundException("Notification", "id", notificationId); // Treat as not found if not owner
        }

        notification.setRead(true);
        Notification updatedNotification = notificationRepository.save(notification);
        return mapNotificationToResponse(updatedNotification);
    }

    @Transactional
    public void deleteNotification(UUID notificationId, UserPrincipal currentUser) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification", "id", notificationId));

        if (!notification.getUser().getId().equals(currentUser.getId())) {
            throw new ResourceNotFoundException("Notification", "id", notificationId); // Treat as not found if not owner
        }

        notificationRepository.delete(notification);
    }

    private NotificationResponse mapNotificationToResponse(Notification notification) {
        return NotificationResponse.builder()
                .id(notification.getId())
                .userId(notification.getUser().getId())
                .message(notification.getMessage())
                .type(notification.getType())
                .relatedEntityId(notification.getRelatedEntityId())
                .isRead(notification.isRead())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}
