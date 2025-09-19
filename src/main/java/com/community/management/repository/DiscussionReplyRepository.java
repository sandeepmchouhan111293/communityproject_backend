package com.community.management.repository;

import com.community.management.entity.DiscussionReply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface DiscussionReplyRepository extends JpaRepository<DiscussionReply, UUID> {
}
