package com.community.management.repository;

import com.community.management.entity.Discussion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface DiscussionRepository extends JpaRepository<Discussion, UUID> {
    long countByCategory(String category);
}