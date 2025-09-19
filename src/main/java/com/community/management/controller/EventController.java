package com.community.management.controller;

import com.community.management.dto.request.CreateEventRequest;
import com.community.management.dto.request.UpdateEventRequest;
import com.community.management.dto.response.EventRegistrationResponse;
import com.community.management.dto.response.EventResponse;
import com.community.management.entity.EventStatus;
import com.community.management.security.UserPrincipal;
import com.community.management.service.EventService;
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
@RequestMapping("/api/events")
public class EventController {

    @Autowired
    private EventService eventService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EventResponse> createEvent(@Valid @RequestBody CreateEventRequest request,
                                                     @AuthenticationPrincipal UserPrincipal currentUser) {
        EventResponse response = eventService.createEvent(request, currentUser);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<EventResponse>> getAllEvents(@RequestParam(required = false) String title,
                                                            @RequestParam(required = false) String location,
                                                            @RequestParam(required = false) EventStatus status) {
        List<EventResponse> response = eventService.getAllEvents(title, location, status);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventResponse> getEventById(@PathVariable UUID id) {
        EventResponse response = eventService.getEventById(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EventResponse> updateEvent(@PathVariable UUID id,
                                                     @Valid @RequestBody UpdateEventRequest request,
                                                     @AuthenticationPrincipal UserPrincipal currentUser) {
        EventResponse response = eventService.updateEvent(id, request, currentUser);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteEvent(@PathVariable UUID id, @AuthenticationPrincipal UserPrincipal currentUser) {
        eventService.deleteEvent(id, currentUser);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/register")
    public ResponseEntity<EventRegistrationResponse> registerForEvent(@PathVariable UUID id, @AuthenticationPrincipal UserPrincipal currentUser) {
        EventRegistrationResponse response = eventService.registerForEvent(id, currentUser);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}/register")
    public ResponseEntity<Void> unregisterFromEvent(@PathVariable UUID id, @AuthenticationPrincipal UserPrincipal currentUser) {
        eventService.unregisterFromEvent(id, currentUser);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/participants")
    public ResponseEntity<List<EventRegistrationResponse>> getEventParticipants(@PathVariable UUID id) {
        List<EventRegistrationResponse> response = eventService.getEventParticipants(id);
        return ResponseEntity.ok(response);
    }
}