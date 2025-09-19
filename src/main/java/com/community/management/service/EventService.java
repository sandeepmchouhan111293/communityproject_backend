package com.community.management.service;

import com.community.management.dto.request.CreateEventRequest;
import com.community.management.dto.request.UpdateEventRequest;
import com.community.management.dto.response.EventRegistrationResponse;
import com.community.management.dto.response.EventResponse;
import com.community.management.entity.Event;
import com.community.management.entity.EventRegistration;
import com.community.management.entity.EventStatus;
import com.community.management.entity.User;
import com.community.management.exception.ResourceNotFoundException;
import com.community.management.exception.ValidationException;
import com.community.management.repository.EventRegistrationRepository;
import com.community.management.repository.EventRepository;
import com.community.management.repository.UserRepository;
import com.community.management.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class EventService {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private EventRegistrationRepository eventRegistrationRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public EventResponse createEvent(CreateEventRequest request, UserPrincipal currentUser) {
        User user = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", currentUser.getId()));

        Event event = new Event();
        event.setCreatedBy(user);
        mapRequestToEvent(request, event);

        Event savedEvent = eventRepository.save(event);
        return mapEventToResponse(savedEvent);
    }

    @Transactional(readOnly = true)
    public List<EventResponse> getAllEvents(String title, String location, EventStatus status) {
        List<Event> events = eventRepository.findAll();

        return events.stream()
                .filter(event -> title == null || event.getTitle().toLowerCase().contains(title.toLowerCase()))
                .filter(event -> location == null || event.getLocation().toLowerCase().contains(location.toLowerCase()))
                .filter(event -> status == null || event.getStatus().equals(status))
                .map(this::mapEventToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public EventResponse getEventById(UUID eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event", "id", eventId));
        return mapEventToResponse(event);
    }

    @Transactional
    public EventResponse updateEvent(UUID eventId, UpdateEventRequest request, UserPrincipal currentUser) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event", "id", eventId));

        boolean isAdmin = currentUser.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (!isAdmin) {
            throw new AccessDeniedException("You do not have permission to update this event.");
        }

        mapRequestToEvent(request, event);

        Event updatedEvent = eventRepository.save(event);
        return mapEventToResponse(updatedEvent);
    }

    @Transactional
    public void deleteEvent(UUID eventId, UserPrincipal currentUser) {
        boolean isAdmin = currentUser.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (!isAdmin) {
            throw new AccessDeniedException("You do not have permission to delete this event.");
        }
        
        if (!eventRepository.existsById(eventId)) {
            throw new ResourceNotFoundException("Event", "id", eventId);
        }
        eventRepository.deleteById(eventId);
    }

    @Transactional
    public EventRegistrationResponse registerForEvent(UUID eventId, UserPrincipal currentUser) {
        User user = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", currentUser.getId()));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event", "id", eventId));

        if (eventRegistrationRepository.findByEventIdAndUserId(eventId, currentUser.getId()).isPresent()) {
            throw new ValidationException("User already registered for this event.");
        }

        if (event.getMaxParticipants() != null && event.getCurrentParticipants() >= event.getMaxParticipants()) {
            throw new ValidationException("Event is full.");
        }

        event.setCurrentParticipants(event.getCurrentParticipants() + 1);
        eventRepository.save(event);

        EventRegistration registration = new EventRegistration();
        registration.setEvent(event);
        registration.setUser(user);

        EventRegistration savedRegistration = eventRegistrationRepository.save(registration);
        return mapRegistrationToResponse(savedRegistration);
    }

    @Transactional
    public void unregisterFromEvent(UUID eventId, UserPrincipal currentUser) {
        EventRegistration registration = eventRegistrationRepository.findByEventIdAndUserId(eventId, currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Registration not found for this event and user."));

        Event event = registration.getEvent();
        event.setCurrentParticipants(event.getCurrentParticipants() - 1);
        eventRepository.save(event);

        eventRegistrationRepository.delete(registration);
    }

    @Transactional(readOnly = true)
    public List<EventRegistrationResponse> getEventParticipants(UUID eventId) {
        return eventRegistrationRepository.findByEventId(eventId).stream()
                .map(this::mapRegistrationToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public long countTotalEvents() {
        return eventRepository.count();
    }

    @Transactional(readOnly = true)
    public long countEventsByStatus(EventStatus status) {
        return eventRepository.countByStatus(status);
    }

    @Transactional(readOnly = true)
    public long countUpcomingEvents() {
        return eventRepository.countByEventDateAfter(LocalDateTime.now());
    }

    private EventResponse mapEventToResponse(Event event) {
        return EventResponse.builder()
                .id(event.getId())
                .title(event.getTitle())
                .description(event.getDescription())
                .eventDate(event.getEventDate())
                .endDate(event.getEndDate())
                .location(event.getLocation())
                .maxParticipants(event.getMaxParticipants())
                .currentParticipants(event.getCurrentParticipants())
                .status(event.getStatus())
                .createdBy(event.getCreatedBy().getId())
                .createdByName(event.getCreatedBy().getFullName())
                .imageUrl(event.getImageUrl())
                .registrationRequired(event.isRegistrationRequired())
                .createdAt(event.getCreatedAt())
                .updatedAt(event.getUpdatedAt())
                .build();
    }

    private EventRegistrationResponse mapRegistrationToResponse(EventRegistration registration) {
        return EventRegistrationResponse.builder()
                .id(registration.getId())
                .eventId(registration.getEvent().getId())
                .eventTitle(registration.getEvent().getTitle())
                .userId(registration.getUser().getId())
                .userName(registration.getUser().getFullName())
                .status(registration.getStatus())
                .registeredAt(registration.getRegisteredAt())
                .build();
    }

    private void mapRequestToEvent(CreateEventRequest request, Event event) {
        event.setTitle(request.getTitle());
        event.setDescription(request.getDescription());
        event.setEventDate(request.getEventDate());
        event.setEndDate(request.getEndDate());
        event.setLocation(request.getLocation());
        event.setMaxParticipants(request.getMaxParticipants());
        event.setImageUrl(request.getImageUrl());
        event.setRegistrationRequired(request.isRegistrationRequired());
    }

    private void mapRequestToEvent(UpdateEventRequest request, Event event) {
        if (request.getTitle() != null) event.setTitle(request.getTitle());
        if (request.getDescription() != null) event.setDescription(request.getDescription());
        if (request.getEventDate() != null) event.setEventDate(request.getEventDate());
        if (request.getEndDate() != null) event.setEndDate(request.getEndDate());
        if (request.getLocation() != null) event.setLocation(request.getLocation());
        if (request.getMaxParticipants() != null) event.setMaxParticipants(request.getMaxParticipants());
        if (request.getStatus() != null) event.setStatus(request.getStatus());
        if (request.getImageUrl() != null) event.setImageUrl(request.getImageUrl());
        if (request.getRegistrationRequired() != null) event.setRegistrationRequired(request.getRegistrationRequired());
    }
}
