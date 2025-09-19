package com.community.management.repository;

import com.community.management.entity.AccessLevel;
import com.community.management.entity.Document;
import com.community.management.entity.DocumentCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DocumentRepository extends JpaRepository<Document, UUID> {
    List<Document> findByAccessLevelIn(List<AccessLevel> accessLevels);
    long countByCategory(DocumentCategory category);
    long countByAccessLevel(AccessLevel accessLevel);
}