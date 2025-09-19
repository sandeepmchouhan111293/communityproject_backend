package com.community.management.repository;

import com.community.management.entity.Event;
import com.community.management.entity.EventStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.UUID;

@Repository
public interface EventRepository extends JpaRepository<Event, UUID> {
    long countByStatus(EventStatus status);
    long countByEventDateAfter(LocalDateTime dateTime);
}