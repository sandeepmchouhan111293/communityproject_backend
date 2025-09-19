package com.community.management.controller;

import com.community.management.dto.request.AddFamilyMemberRequest;
import com.community.management.dto.request.UpdateFamilyMemberRequest;
import com.community.management.dto.response.FamilyMemberResponse;
import com.community.management.security.UserPrincipal;
import com.community.management.service.FamilyService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/family/members")
public class FamilyController {

    @Autowired
    private FamilyService familyService;

    @PostMapping
    public ResponseEntity<FamilyMemberResponse> addFamilyMember(@AuthenticationPrincipal UserPrincipal currentUser,
                                                                @Valid @RequestBody AddFamilyMemberRequest request) {
        FamilyMemberResponse response = familyService.addMember(currentUser.getId(), request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<FamilyMemberResponse>> getFamilyMembers(@AuthenticationPrincipal UserPrincipal currentUser) {
        List<FamilyMemberResponse> response = familyService.getMembers(currentUser.getId());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FamilyMemberResponse> getFamilyMemberById(@PathVariable UUID id, @AuthenticationPrincipal UserPrincipal currentUser) {
        FamilyMemberResponse response = familyService.getMemberById(id, currentUser.getId());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<FamilyMemberResponse> updateFamilyMember(@PathVariable UUID id,
                                                                   @AuthenticationPrincipal UserPrincipal currentUser,
                                                                   @Valid @RequestBody UpdateFamilyMemberRequest request) {
        FamilyMemberResponse response = familyService.updateMember(id, currentUser.getId(), request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFamilyMember(@PathVariable UUID id, @AuthenticationPrincipal UserPrincipal currentUser) {
        familyService.deleteMember(id, currentUser.getId());
        return ResponseEntity.noContent().build();
    }
}
