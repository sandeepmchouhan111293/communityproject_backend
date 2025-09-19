package com.community.management.repository;

import com.community.management.entity.EventRegistration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EventRegistrationRepository extends JpaRepository<EventRegistration, UUID> {
    Optional<EventRegistration> findByEventIdAndUserId(UUID eventId, UUID userId);
    List<EventRegistration> findByEventId(UUID eventId);
}
