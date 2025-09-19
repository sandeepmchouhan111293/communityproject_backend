package com.community.management.service;

import com.community.management.dto.request.CreateDiscussionReplyRequest;
import com.community.management.dto.request.CreateDiscussionRequest;
import com.community.management.dto.request.UpdateDiscussionReplyRequest;
import com.community.management.dto.request.UpdateDiscussionRequest;
import com.community.management.dto.response.DiscussionReplyResponse;
import com.community.management.dto.response.DiscussionResponse;
import com.community.management.entity.Discussion;
import com.community.management.entity.DiscussionReply;
import com.community.management.entity.User;
import com.community.management.exception.ResourceNotFoundException;
import com.community.management.repository.DiscussionReplyRepository;
import com.community.management.repository.DiscussionRepository;
import com.community.management.repository.UserRepository;
import com.community.management.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class DiscussionService {

    @Autowired
    private DiscussionRepository discussionRepository;

    @Autowired
    private DiscussionReplyRepository discussionReplyRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public DiscussionResponse createDiscussion(CreateDiscussionRequest request, UserPrincipal currentUser) {
        User user = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", currentUser.getId()));

        Discussion discussion = new Discussion();
        discussion.setCreatedBy(user);
        discussion.setTitle(request.getTitle());
        discussion.setContent(request.getContent());
        discussion.setCategory(request.getCategory());

        Discussion savedDiscussion = discussionRepository.save(discussion);
        return mapDiscussionToResponse(savedDiscussion);
    }

    @Transactional(readOnly = true)
    public List<DiscussionResponse> getAllDiscussions(String title, String category) {
        List<Discussion> discussions = discussionRepository.findAll();

        return discussions.stream()
                .filter(disc -> title == null || disc.getTitle().toLowerCase().contains(title.toLowerCase()))
                .filter(disc -> category == null || disc.getCategory().toLowerCase().contains(category.toLowerCase()))
                .map(this::mapDiscussionToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public DiscussionResponse getDiscussionById(UUID discussionId) {
        Discussion discussion = discussionRepository.findById(discussionId)
                .orElseThrow(() -> new ResourceNotFoundException("Discussion", "id", discussionId));
        discussion.setViewCount(discussion.getViewCount() + 1);
        discussionRepository.save(discussion);
        return mapDiscussionToResponse(discussion);
    }

    @Transactional
    public DiscussionResponse updateDiscussion(UUID discussionId, UpdateDiscussionRequest request, UserPrincipal currentUser) {
        Discussion discussion = discussionRepository.findById(discussionId)
                .orElseThrow(() -> new ResourceNotFoundException("Discussion", "id", discussionId));

        boolean isAdmin = currentUser.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        boolean isOwner = discussion.getCreatedBy().getId().equals(currentUser.getId());
        if (!isAdmin && !isOwner) {
            throw new AccessDeniedException("You do not have permission to update this discussion.");
        }

        if (request.getTitle() != null) discussion.setTitle(request.getTitle());
        if (request.getContent() != null) discussion.setContent(request.getContent());
        if (request.getCategory() != null) discussion.setCategory(request.getCategory());
        if (request.getIsPinned() != null) discussion.setPinned(request.getIsPinned());
        if (request.getIsLocked() != null) discussion.setLocked(request.getIsLocked());

        Discussion updatedDiscussion = discussionRepository.save(discussion);
        return mapDiscussionToResponse(updatedDiscussion);
    }

    @Transactional
    public void deleteDiscussion(UUID discussionId, UserPrincipal currentUser) {
        Discussion discussion = discussionRepository.findById(discussionId)
                .orElseThrow(() -> new ResourceNotFoundException("Discussion", "id", discussionId));

        boolean isAdmin = currentUser.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        boolean isOwner = discussion.getCreatedBy().getId().equals(currentUser.getId());
        if (!isAdmin && !isOwner) {
            throw new AccessDeniedException("You do not have permission to delete this discussion.");
        }

        discussionRepository.delete(discussion);
    }

    @Transactional
    public DiscussionReplyResponse addReply(UUID discussionId, CreateDiscussionReplyRequest request, UserPrincipal currentUser) {
        User user = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", currentUser.getId()));
        Discussion discussion = discussionRepository.findById(discussionId)
                .orElseThrow(() -> new ResourceNotFoundException("Discussion", "id", discussionId));

        DiscussionReply reply = new DiscussionReply();
        reply.setCreatedBy(user);
        reply.setDiscussion(discussion);
        reply.setContent(request.getContent());

        if (request.getParentReplyId() != null) {
            DiscussionReply parentReply = discussionReplyRepository.findById(request.getParentReplyId())
                    .orElseThrow(() -> new ResourceNotFoundException("Parent Reply", "id", request.getParentReplyId()));
            reply.setParentReply(parentReply);
        }

        discussion.setReplyCount(discussion.getReplyCount() + 1);
        discussionRepository.save(discussion);

        DiscussionReply savedReply = discussionReplyRepository.save(reply);
        return mapReplyToResponse(savedReply);
    }
    
    @Transactional(readOnly = true)
    public List<DiscussionReplyResponse> getReplies(UUID discussionId) {
        Discussion discussion = discussionRepository.findById(discussionId)
                .orElseThrow(() -> new ResourceNotFoundException("Discussion", "id", discussionId));
        return discussion.getReplies().stream()
                .map(this::mapReplyToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public DiscussionReplyResponse updateReply(UUID replyId, UpdateDiscussionReplyRequest request, UserPrincipal currentUser) {
        DiscussionReply reply = discussionReplyRepository.findById(replyId)
                .orElseThrow(() -> new ResourceNotFoundException("Reply", "id", replyId));

        boolean isAdmin = currentUser.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        boolean isOwner = reply.getCreatedBy().getId().equals(currentUser.getId());
        if (!isAdmin && !isOwner) {
            throw new AccessDeniedException("You do not have permission to update this reply.");
        }

        if (request.getContent() != null) reply.setContent(request.getContent());

        DiscussionReply updatedReply = discussionReplyRepository.save(reply);
        return mapReplyToResponse(updatedReply);
    }

    @Transactional
    public void deleteReply(UUID replyId, UserPrincipal currentUser) {
        DiscussionReply reply = discussionReplyRepository.findById(replyId)
                .orElseThrow(() -> new ResourceNotFoundException("Reply", "id", replyId));

        boolean isAdmin = currentUser.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        boolean isOwner = reply.getCreatedBy().getId().equals(currentUser.getId());
        if (!isAdmin && !isOwner) {
            throw new AccessDeniedException("You do not have permission to delete this reply.");
        }

        Discussion discussion = reply.getDiscussion();
        discussion.setReplyCount(discussion.getReplyCount() - 1);
        discussionRepository.save(discussion);

        discussionReplyRepository.delete(reply);
    }

    @Transactional(readOnly = true)
    public long countTotalDiscussions() {
        return discussionRepository.count();
    }

    @Transactional(readOnly = true)
    public long countDiscussionsByCategory(String category) {
        return discussionRepository.countByCategory(category);
    }

    private DiscussionResponse mapDiscussionToResponse(Discussion discussion) {
        return DiscussionResponse.builder()
                .id(discussion.getId())
                .title(discussion.getTitle())
                .content(discussion.getContent())
                .category(discussion.getCategory())
                .createdBy(discussion.getCreatedBy().getId())
                .createdByName(discussion.getCreatedBy().getFullName())
                .isPinned(discussion.isPinned())
                .isLocked(discussion.isLocked())
                .viewCount(discussion.getViewCount())
                .replyCount(discussion.getReplyCount())
                .replies(discussion.getReplies().stream().map(this::mapReplyToResponse).collect(Collectors.toList()))
                .createdAt(discussion.getCreatedAt())
                .updatedAt(discussion.getUpdatedAt())
                .build();
    }

    private DiscussionReplyResponse mapReplyToResponse(DiscussionReply reply) {
        return DiscussionReplyResponse.builder()
                .id(reply.getId())
                .discussionId(reply.getDiscussion().getId())
                .content(reply.getContent())
                .createdBy(reply.getCreatedBy().getId())
                .createdByName(reply.getCreatedBy().getFullName())
                .parentReplyId(reply.getParentReply() != null ? reply.getParentReply().getId() : null)
                .createdAt(reply.getCreatedAt())
                .updatedAt(reply.getUpdatedAt())
                .build();
    }
}
