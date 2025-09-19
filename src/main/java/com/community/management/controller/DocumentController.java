package com.community.management.controller;

import com.community.management.dto.request.CreateDocumentRequest;
import com.community.management.dto.request.UpdateDocumentRequest;
import com.community.management.dto.response.DocumentResponse;
import com.community.management.entity.DocumentCategory;
import com.community.management.security.UserPrincipal;
import com.community.management.service.DocumentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {

    @Autowired
    private DocumentService documentService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DocumentResponse> uploadDocument(@Valid @RequestPart("request") CreateDocumentRequest request,
                                                           @RequestPart("file") MultipartFile file,
                                                           @AuthenticationPrincipal UserPrincipal currentUser) {
        DocumentResponse response = documentService.uploadDocument(request, file, currentUser);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<DocumentResponse>> getDocuments(@AuthenticationPrincipal UserPrincipal currentUser,
                                                               @RequestParam(required = false) String title,
                                                               @RequestParam(required = false) DocumentCategory category) {
        List<DocumentResponse> response = documentService.getDocuments(currentUser, title, category);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DocumentResponse> getDocumentById(@PathVariable UUID id, @AuthenticationPrincipal UserPrincipal currentUser) {
        DocumentResponse response = documentService.getDocumentById(id, currentUser);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DocumentResponse> updateDocument(@PathVariable UUID id,
                                                           @Valid @RequestBody UpdateDocumentRequest request,
                                                           @AuthenticationPrincipal UserPrincipal currentUser) {
        DocumentResponse response = documentService.updateDocument(id, request, currentUser);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteDocument(@PathVariable UUID id, @AuthenticationPrincipal UserPrincipal currentUser) {
        documentService.deleteDocument(id, currentUser);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/categories")
    public ResponseEntity<DocumentCategory[]> getDocumentCategories() {
        return ResponseEntity.ok(com.community.management.entity.DocumentCategory.values());
    }
}