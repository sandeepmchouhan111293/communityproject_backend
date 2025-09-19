package com.community.management.controller;

import com.community.management.dto.request.UpdateDirectoryEntryRequest;
import com.community.management.dto.response.DirectoryEntryResponse;
import com.community.management.security.UserPrincipal;
import com.community.management.service.DirectoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/directory")
public class DirectoryController {

    @Autowired
    private DirectoryService directoryService;

    @GetMapping
    public ResponseEntity<List<DirectoryEntryResponse>> getAllDirectoryEntries(@RequestParam(required = false) String displayName,
                                                                               @RequestParam(required = false) String city,
                                                                               @RequestParam(required = false) String state,
                                                                               @RequestParam(required = false) String district,
                                                                               @RequestParam(required = false) String communityName) {
        List<DirectoryEntryResponse> entries = directoryService.getAllDirectoryEntries(displayName, city, state, district, communityName);
        return ResponseEntity.ok(entries);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DirectoryEntryResponse> getDirectoryEntryById(@PathVariable UUID id) {
        DirectoryEntryResponse entry = directoryService.getDirectoryEntryById(id);
        return ResponseEntity.ok(entry);
    }

    @GetMapping("/me")
    public ResponseEntity<DirectoryEntryResponse> getMyDirectoryEntry(@AuthenticationPrincipal UserPrincipal currentUser) {
        DirectoryEntryResponse entry = directoryService.getMyDirectoryEntry(currentUser);
        return ResponseEntity.ok(entry);
    }

    @PutMapping("/me")
    public ResponseEntity<DirectoryEntryResponse> updateMyDirectoryEntry(@Valid @RequestBody UpdateDirectoryEntryRequest request,
                                                                       @AuthenticationPrincipal UserPrincipal currentUser) {
        DirectoryEntryResponse updatedEntry = directoryService.updateMyDirectoryEntry(request, currentUser);
        return ResponseEntity.ok(updatedEntry);
    }
}