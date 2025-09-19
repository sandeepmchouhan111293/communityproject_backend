package com.community.management.controller;

import com.community.management.dto.request.CreateDiscussionReplyRequest;
import com.community.management.dto.request.CreateDiscussionRequest;
import com.community.management.dto.request.UpdateDiscussionReplyRequest;
import com.community.management.dto.request.UpdateDiscussionRequest;
import com.community.management.dto.response.DiscussionReplyResponse;
import com.community.management.dto.response.DiscussionResponse;
import com.community.management.security.UserPrincipal;
import com.community.management.service.DiscussionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/discussions")
public class DiscussionController {

    @Autowired
    private DiscussionService discussionService;

    @PostMapping
    public ResponseEntity<DiscussionResponse> createDiscussion(@Valid @RequestBody CreateDiscussionRequest request,
                                                               @AuthenticationPrincipal UserPrincipal currentUser) {
        DiscussionResponse response = discussionService.createDiscussion(request, currentUser);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<DiscussionResponse>> getAllDiscussions(@RequestParam(required = false) String title,
                                                                      @RequestParam(required = false) String category) {
        List<DiscussionResponse> response = discussionService.getAllDiscussions(title, category);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DiscussionResponse> getDiscussionById(@PathVariable UUID id) {
        DiscussionResponse response = discussionService.getDiscussionById(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DiscussionResponse> updateDiscussion(@PathVariable UUID id,
                                                               @Valid @RequestBody UpdateDiscussionRequest request,
                                                               @AuthenticationPrincipal UserPrincipal currentUser) {
        DiscussionResponse response = discussionService.updateDiscussion(id, request, currentUser);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDiscussion(@PathVariable UUID id, @AuthenticationPrincipal UserPrincipal currentUser) {
        discussionService.deleteDiscussion(id, currentUser);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/replies")
    public ResponseEntity<DiscussionReplyResponse> addReply(@PathVariable UUID id,
                                                            @Valid @RequestBody CreateDiscussionReplyRequest request,
                                                            @AuthenticationPrincipal UserPrincipal currentUser) {
        DiscussionReplyResponse response = discussionService.addReply(id, request, currentUser);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}/replies")
    public ResponseEntity<List<DiscussionReplyResponse>> getReplies(@PathVariable UUID id) {
        List<DiscussionReplyResponse> response = discussionService.getReplies(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/replies/{replyId}")
    public ResponseEntity<DiscussionReplyResponse> updateReply(@PathVariable UUID replyId,
                                                               @Valid @RequestBody UpdateDiscussionReplyRequest request,
                                                               @AuthenticationPrincipal UserPrincipal currentUser) {
        DiscussionReplyResponse response = discussionService.updateReply(replyId, request, currentUser);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/replies/{replyId}")
    public ResponseEntity<Void> deleteReply(@PathVariable UUID replyId, @AuthenticationPrincipal UserPrincipal currentUser) {
        discussionService.deleteReply(replyId, currentUser);
        return ResponseEntity.noContent().build();
    }
}