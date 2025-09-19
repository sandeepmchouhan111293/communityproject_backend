package com.community.management.service;

import com.community.management.dto.request.CreateVolunteerOpportunityRequest;
import com.community.management.dto.request.UpdateVolunteerOpportunityRequest;
import com.community.management.dto.request.UpdateVolunteerRegistrationRequest;
import com.community.management.dto.response.VolunteerOpportunityResponse;
import com.community.management.dto.response.VolunteerRegistrationResponse;
import com.community.management.entity.RegistrationStatus;
import com.community.management.entity.User;
import com.community.management.entity.VolunteerOpportunity;
import com.community.management.entity.VolunteerRegistration;
import com.community.management.entity.VolunteerStatus;
import com.community.management.exception.ResourceNotFoundException;
import com.community.management.exception.ValidationException;
import com.community.management.repository.UserRepository;
import com.community.management.repository.VolunteerOpportunityRepository;
import com.community.management.repository.VolunteerRegistrationRepository;
import com.community.management.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class VolunteerService {

    @Autowired
    private VolunteerOpportunityRepository opportunityRepository;

    @Autowired
    private VolunteerRegistrationRepository registrationRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public VolunteerOpportunityResponse createOpportunity(CreateVolunteerOpportunityRequest request, UserPrincipal currentUser) {
        User user = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", currentUser.getId()));

        VolunteerOpportunity opportunity = new VolunteerOpportunity();
        opportunity.setCreatedBy(user);
        mapRequestToOpportunity(request, opportunity);

        VolunteerOpportunity savedOpportunity = opportunityRepository.save(opportunity);
        return mapOpportunityToResponse(savedOpportunity);
    }

    @Transactional(readOnly = true)
    public List<VolunteerOpportunityResponse> getAllOpportunities(String title, String location, VolunteerStatus status) {
        List<VolunteerOpportunity> opportunities = opportunityRepository.findAll();

        return opportunities.stream()
                .filter(opp -> title == null || opp.getTitle().toLowerCase().contains(title.toLowerCase()))
                .filter(opp -> location == null || opp.getLocation().toLowerCase().contains(location.toLowerCase()))
                .filter(opp -> status == null || opp.getStatus().equals(status))
                .map(this::mapOpportunityToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public VolunteerOpportunityResponse getOpportunityById(UUID opportunityId) {
        VolunteerOpportunity opportunity = opportunityRepository.findById(opportunityId)
                .orElseThrow(() -> new ResourceNotFoundException("VolunteerOpportunity", "id", opportunityId));
        return mapOpportunityToResponse(opportunity);
    }

    @Transactional
    public VolunteerOpportunityResponse updateOpportunity(UUID opportunityId, UpdateVolunteerOpportunityRequest request, UserPrincipal currentUser) {
        VolunteerOpportunity opportunity = opportunityRepository.findById(opportunityId)
                .orElseThrow(() -> new ResourceNotFoundException("VolunteerOpportunity", "id", opportunityId));

        boolean isAdmin = currentUser.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (!isAdmin) {
            throw new AccessDeniedException("You do not have permission to update this opportunity.");
        }

        mapRequestToOpportunity(request, opportunity);

        VolunteerOpportunity updatedOpportunity = opportunityRepository.save(opportunity);
        return mapOpportunityToResponse(updatedOpportunity);
    }

    @Transactional
    public void deleteOpportunity(UUID opportunityId, UserPrincipal currentUser) {
        boolean isAdmin = currentUser.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (!isAdmin) {
            throw new AccessDeniedException("You do not have permission to delete this opportunity.");
        }
        
        if (!opportunityRepository.existsById(opportunityId)) {
            throw new ResourceNotFoundException("VolunteerOpportunity", "id", opportunityId);
        }
        opportunityRepository.deleteById(opportunityId);
    }

    @Transactional
    public VolunteerRegistrationResponse registerForOpportunity(UUID opportunityId, UserPrincipal currentUser) {
        User user = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", currentUser.getId()));
        VolunteerOpportunity opportunity = opportunityRepository.findById(opportunityId)
                .orElseThrow(() -> new ResourceNotFoundException("VolunteerOpportunity", "id", opportunityId));

        if (registrationRepository.findByOpportunityIdAndUserId(opportunityId, currentUser.getId()).isPresent()) {
            throw new ValidationException("User already registered for this opportunity.");
        }

        if (opportunity.getMaxVolunteers() != null && opportunity.getCurrentVolunteers() >= opportunity.getMaxVolunteers()) {
            throw new ValidationException("Opportunity is full.");
        }

        opportunity.setCurrentVolunteers(opportunity.getCurrentVolunteers() + 1);
        opportunityRepository.save(opportunity);

        VolunteerRegistration registration = new VolunteerRegistration();
        registration.setOpportunity(opportunity);
        registration.setUser(user);

        VolunteerRegistration savedRegistration = registrationRepository.save(registration);
        return mapRegistrationToResponse(savedRegistration);
    }

    @Transactional
    public void unregisterFromOpportunity(UUID opportunityId, UserPrincipal currentUser) {
        VolunteerRegistration registration = registrationRepository.findByOpportunityIdAndUserId(opportunityId, currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Registration not found for this opportunity and user."));

        VolunteerOpportunity opportunity = registration.getOpportunity();
        opportunity.setCurrentVolunteers(opportunity.getCurrentVolunteers() - 1);
        opportunityRepository.save(opportunity);

        registrationRepository.delete(registration);
    }

    @Transactional(readOnly = true)
    public List<VolunteerRegistrationResponse> getUserRegistrations(UUID userId) {
        return registrationRepository.findByUserId(userId).stream()
                .map(this::mapRegistrationToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<VolunteerRegistrationResponse> getOpportunityRegistrations(UUID opportunityId) {
        return registrationRepository.findByOpportunityId(opportunityId).stream()
                .map(this::mapRegistrationToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public VolunteerRegistrationResponse updateRegistrationStatus(UUID registrationId, UpdateVolunteerRegistrationRequest request, UserPrincipal currentUser) {
        VolunteerRegistration registration = registrationRepository.findById(registrationId)
                .orElseThrow(() -> new ResourceNotFoundException("VolunteerRegistration", "id", registrationId));

        boolean isAdmin = currentUser.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        boolean isOpportunityCreator = registration.getOpportunity().getCreatedBy().getId().equals(currentUser.getId());
        if (!isAdmin && !isOpportunityCreator) {
            throw new AccessDeniedException("You do not have permission to update this registration status.");
        }

        if (request.getStatus() != null) registration.setStatus(request.getStatus());
        if (request.getNotes() != null) registration.setNotes(request.getNotes());

        VolunteerRegistration updatedRegistration = registrationRepository.save(registration);
        return mapRegistrationToResponse(updatedRegistration);
    }

    @Transactional(readOnly = true)
    public long countTotalOpportunities() {
        return opportunityRepository.count();
    }

    @Transactional(readOnly = true)
    public long countOpportunitiesByStatus(VolunteerStatus status) {
        return opportunityRepository.countByStatus(status);
    }

    private VolunteerOpportunityResponse mapOpportunityToResponse(VolunteerOpportunity opportunity) {
        return VolunteerOpportunityResponse.builder()
                .id(opportunity.getId())
                .title(opportunity.getTitle())
                .description(opportunity.getDescription())
                .requirements(opportunity.getRequirements())
                .location(opportunity.getLocation())
                .dateTime(opportunity.getDateTime())
                .durationHours(opportunity.getDurationHours())
                .maxVolunteers(opportunity.getMaxVolunteers())
                .currentVolunteers(opportunity.getCurrentVolunteers())
                .status(opportunity.getStatus())
                .createdBy(opportunity.getCreatedBy().getId())
                .createdByName(opportunity.getCreatedBy().getFullName())
                .createdAt(opportunity.getCreatedAt())
                .updatedAt(opportunity.getUpdatedAt())
                .build();
    }

    private VolunteerRegistrationResponse mapRegistrationToResponse(VolunteerRegistration registration) {
        return VolunteerRegistrationResponse.builder()
                .id(registration.getId())
                .opportunityId(registration.getOpportunity().getId())
                .opportunityTitle(registration.getOpportunity().getTitle())
                .userId(registration.getUser().getId())
                .userName(registration.getUser().getFullName())
                .status(registration.getStatus())
                .notes(registration.getNotes())
                .registeredAt(registration.getRegisteredAt())
                .build();
    }

    private void mapRequestToOpportunity(CreateVolunteerOpportunityRequest request, VolunteerOpportunity opportunity) {
        opportunity.setTitle(request.getTitle());
        opportunity.setDescription(request.getDescription());
        opportunity.setRequirements(request.getRequirements());
        opportunity.setLocation(request.getLocation());
        opportunity.setDateTime(request.getDateTime());
        opportunity.setDurationHours(request.getDurationHours());
        opportunity.setMaxVolunteers(request.getMaxVolunteers());
    }

    private void mapRequestToOpportunity(UpdateVolunteerOpportunityRequest request, VolunteerOpportunity opportunity) {
        if (request.getTitle() != null) opportunity.setTitle(request.getTitle());
        if (request.getDescription() != null) opportunity.setDescription(request.getDescription());
        if (request.getRequirements() != null) opportunity.setRequirements(request.getRequirements());
        if (request.getLocation() != null) opportunity.setLocation(request.getLocation());
        if (request.getDateTime() != null) opportunity.setDateTime(request.getDateTime());
        if (request.getDurationHours() != null) opportunity.setDurationHours(request.getDurationHours());
        if (request.getMaxVolunteers() != null) opportunity.setMaxVolunteers(request.getMaxVolunteers());
        if (request.getStatus() != null) opportunity.setStatus(request.getStatus());
    }
}
