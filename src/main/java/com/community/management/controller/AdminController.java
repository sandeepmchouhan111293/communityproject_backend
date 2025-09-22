package com.community.management.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.community.management.dto.request.UpdateUserRoleRequest;
import com.community.management.dto.response.ApiResponse;
import com.community.management.dto.response.AuditLogResponse;
import com.community.management.dto.response.UserAdminResponse;
import com.community.management.entity.EventStatus;
import com.community.management.entity.UserRole;
import com.community.management.entity.VolunteerStatus;
import com.community.management.service.AuditService;
import com.community.management.service.DiscussionService;
import com.community.management.service.DocumentService;
import com.community.management.service.EventService;
import com.community.management.service.UserService;
import com.community.management.service.VolunteerService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private AuditService auditService;

    @Autowired
    private EventService eventService;

    @Autowired
    private DiscussionService discussionService;

    @Autowired
    private VolunteerService volunteerService;

    @Autowired
    private DocumentService documentService;

    @GetMapping("/users")
    public ResponseEntity<List<UserAdminResponse>> getAllUsers() {
        List<UserAdminResponse> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<UserAdminResponse> getUserById(@PathVariable UUID id) {
        UserAdminResponse user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/users/{id}/role")
    public ResponseEntity<UserAdminResponse> updateUserRole(@PathVariable UUID id,
            @Valid @RequestBody UpdateUserRoleRequest request) {
        UserAdminResponse updatedUser = userService.updateUserRole(id, request.getRole());
        auditService.logAction(null, "UPDATE_USER_ROLE", "User", id, null, updatedUser); // Log as system action
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<ApiResponse> deleteUser(@PathVariable UUID id) {
        userService.deleteUser(id);
        auditService.logAction(null, "DELETE_USER", "User", id, null, null); // Log as system action
        return ResponseEntity.ok(new ApiResponse(true, "User deleted successfully."));
    }

    @GetMapping("/audit-logs")
    public ResponseEntity<List<AuditLogResponse>> getAllAuditLogs() {
        List<AuditLogResponse> auditLogs = auditService.getAllAuditLogs();
        return ResponseEntity.ok(auditLogs);
    }

    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Long>> getDashboardStats() {
        Map<String, Long> stats = new HashMap<>();
        stats.put("totalUsers", userService.countTotalUsers());
        stats.put("activeUsers", userService.countActiveUsers());
        stats.put("adminUsers", userService.countUsersByRole(UserRole.ADMIN));
        stats.put("memberUsers", userService.countUsersByRole(UserRole.MEMBER));
        stats.put("totalEvents", eventService.countTotalEvents());
        stats.put("upcomingEvents", eventService.countUpcomingEvents());
        stats.put("completedEvents", eventService.countEventsByStatus(EventStatus.COMPLETED));
        stats.put("totalDiscussions", discussionService.countTotalDiscussions());
        stats.put("totalVolunteerOpportunities", volunteerService.countTotalOpportunities());
        stats.put("activeVolunteerOpportunities", volunteerService.countOpportunitiesByStatus(VolunteerStatus.ACTIVE));
        stats.put("totalDocuments", documentService.countTotalDocuments());
        stats.put("publicDocuments",
                documentService.countDocumentsByAccessLevel(com.community.management.entity.AccessLevel.PUBLIC));
        stats.put("memberDocuments",
                documentService.countDocumentsByAccessLevel(com.community.management.entity.AccessLevel.MEMBER));

        return ResponseEntity.ok(stats);
    }

    @GetMapping("/system-stats")
    public ResponseEntity<String> getSystemStats() {
        return ResponseEntity.ok("System Statistics Placeholder");
    }

    @GetMapping("/health")
    public ResponseEntity<String> getHealth() {
        return ResponseEntity.ok("Healthy");
    }
}