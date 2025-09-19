package com.community.management.repository;

import com.community.management.entity.VolunteerOpportunity;
import com.community.management.entity.VolunteerStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface VolunteerOpportunityRepository extends JpaRepository<VolunteerOpportunity, UUID> {
    List<VolunteerOpportunity> findByCreatedBy_Id(UUID userId);
    long countByStatus(VolunteerStatus status);
}