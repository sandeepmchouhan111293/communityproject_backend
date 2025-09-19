package com.community.management.repository;

import com.community.management.entity.VolunteerRegistration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface VolunteerRegistrationRepository extends JpaRepository<VolunteerRegistration, UUID> {
    Optional<VolunteerRegistration> findByOpportunityIdAndUserId(UUID opportunityId, UUID userId);
    List<VolunteerRegistration> findByUserId(UUID userId);
    List<VolunteerRegistration> findByOpportunityId(UUID opportunityId);
}
