package com.community.management.controller;

import com.community.management.dto.request.CreateVolunteerOpportunityRequest;
import com.community.management.dto.request.UpdateVolunteerOpportunityRequest;
import com.community.management.dto.request.UpdateVolunteerRegistrationRequest;
import com.community.management.dto.response.VolunteerOpportunityResponse;
import com.community.management.dto.response.VolunteerRegistrationResponse;
import com.community.management.entity.VolunteerStatus;
import com.community.management.security.UserPrincipal;
import com.community.management.service.VolunteerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/volunteers")
public class VolunteerController {

    @Autowired
    private VolunteerService volunteerService;

    @PostMapping("/opportunities")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<VolunteerOpportunityResponse> createOpportunity(@Valid @RequestBody CreateVolunteerOpportunityRequest request,
                                                                        @AuthenticationPrincipal UserPrincipal currentUser) {
        VolunteerOpportunityResponse response = volunteerService.createOpportunity(request, currentUser);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/opportunities")
    public ResponseEntity<List<VolunteerOpportunityResponse>> getAllOpportunities(@RequestParam(required = false) String title,
                                                                                @RequestParam(required = false) String location,
                                                                                @RequestParam(required = false) VolunteerStatus status) {
        List<VolunteerOpportunityResponse> response = volunteerService.getAllOpportunities(title, location, status);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/opportunities/{id}")
    public ResponseEntity<VolunteerOpportunityResponse> getOpportunityById(@PathVariable UUID id) {
        VolunteerOpportunityResponse response = volunteerService.getOpportunityById(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/opportunities/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<VolunteerOpportunityResponse> updateOpportunity(@PathVariable UUID id,
                                                                        @Valid @RequestBody UpdateVolunteerOpportunityRequest request,
                                                                        @AuthenticationPrincipal UserPrincipal currentUser) {
        VolunteerOpportunityResponse response = volunteerService.updateOpportunity(id, request, currentUser);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/opportunities/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteOpportunity(@PathVariable UUID id, @AuthenticationPrincipal UserPrincipal currentUser) {
        volunteerService.deleteOpportunity(id, currentUser);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/opportunities/{id}/register")
    public ResponseEntity<VolunteerRegistrationResponse> registerForOpportunity(@PathVariable UUID id, @AuthenticationPrincipal UserPrincipal currentUser) {
        VolunteerRegistrationResponse response = volunteerService.registerForOpportunity(id, currentUser);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @DeleteMapping("/opportunities/{id}/register")
    public ResponseEntity<Void> unregisterFromOpportunity(@PathVariable UUID id, @AuthenticationPrincipal UserPrincipal currentUser) {
        volunteerService.unregisterFromOpportunity(id, currentUser);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/my-registrations")
    public ResponseEntity<List<VolunteerRegistrationResponse>> getMyRegistrations(@AuthenticationPrincipal UserPrincipal currentUser) {
        List<VolunteerRegistrationResponse> response = volunteerService.getUserRegistrations(currentUser.getId());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/opportunities/{id}/registrations")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<VolunteerRegistrationResponse>> getOpportunityRegistrations(@PathVariable UUID id) {
        List<VolunteerRegistrationResponse> response = volunteerService.getOpportunityRegistrations(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/registrations/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<VolunteerRegistrationResponse> updateRegistrationStatus(@PathVariable UUID id,
                                                                                @Valid @RequestBody UpdateVolunteerRegistrationRequest request,
                                                                                @AuthenticationPrincipal UserPrincipal currentUser) {
        VolunteerRegistrationResponse response = volunteerService.updateRegistrationStatus(id, request, currentUser);
        return ResponseEntity.ok(response);
    }
}