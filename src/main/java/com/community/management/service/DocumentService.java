package com.community.management.service;

import com.community.management.dto.request.CreateDocumentRequest;
import com.community.management.dto.request.UpdateDocumentRequest;
import com.community.management.dto.response.DocumentResponse;
import com.community.management.entity.AccessLevel;
import com.community.management.entity.Document;
import com.community.management.entity.DocumentCategory;
import com.community.management.entity.User;
import com.community.management.exception.ResourceNotFoundException;
import com.community.management.repository.DocumentRepository;
import com.community.management.repository.UserRepository;
import com.community.management.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class DocumentService {

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    @Qualifier("localFileStorageService")
    private FileStorageService fileStorageService;

    @Transactional
    public DocumentResponse uploadDocument(CreateDocumentRequest request, MultipartFile file, UserPrincipal currentUser) {
        User user = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", currentUser.getId()));

        String fileName = fileStorageService.storeFile(file);
        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/files/")
                .path(fileName)
                .toUriString();

        Document document = new Document();
        document.setTitle(request.getTitle());
        document.setDescription(request.getDescription());
        document.setCategory(request.getCategory());
        document.setAccessLevel(request.getAccessLevel());
        document.setUploadedBy(user);
        document.setFileType(file.getContentType());
        document.setFileSize(String.valueOf(file.getSize()));
        document.setFileUrl(fileDownloadUri);

        Document savedDocument = documentRepository.save(document);
        return mapDocumentToResponse(savedDocument);
    }

    @Transactional(readOnly = true)
    public List<DocumentResponse> getDocuments(UserPrincipal currentUser, String title, DocumentCategory category) {
        List<AccessLevel> accessibleLevels = getAccessibleLevels(currentUser.getAuthorities().stream().findFirst().get().getAuthority());
        List<Document> documents = documentRepository.findByAccessLevelIn(accessibleLevels);

        return documents.stream()
                .filter(doc -> title == null || doc.getTitle().toLowerCase().contains(title.toLowerCase()))
                .filter(doc -> category == null || doc.getCategory().equals(category))
                .map(this::mapDocumentToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public DocumentResponse getDocumentById(UUID documentId, UserPrincipal currentUser) {
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new ResourceNotFoundException("Document", "id", documentId));
        
        List<AccessLevel> accessibleLevels = getAccessibleLevels(currentUser.getAuthorities().stream().findFirst().get().getAuthority());
        if (!accessibleLevels.contains(document.getAccessLevel())) {
            throw new AccessDeniedException("You do not have permission to view this document.");
        }

        return mapDocumentToResponse(document);
    }

    @Transactional
    public DocumentResponse updateDocument(UUID documentId, UpdateDocumentRequest request, UserPrincipal currentUser) {
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new ResourceNotFoundException("Document", "id", documentId));

        // Authorization check: only admin or owner can update
        boolean isAdmin = currentUser.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        boolean isOwner = document.getUploadedBy().getId().equals(currentUser.getId());
        if (!isAdmin && !isOwner) {
            throw new AccessDeniedException("You do not have permission to update this document.");
        }

        if (request.getTitle() != null) document.setTitle(request.getTitle());
        if (request.getDescription() != null) document.setDescription(request.getDescription());
        if (request.getCategory() != null) document.setCategory(request.getCategory());
        if (request.getAccessLevel() != null) document.setAccessLevel(request.getAccessLevel());

        Document updatedDocument = documentRepository.save(document);
        return mapDocumentToResponse(updatedDocument);
    }

    @Transactional
    public void deleteDocument(UUID documentId, UserPrincipal currentUser) {
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new ResourceNotFoundException("Document", "id", documentId));

        // Authorization check: only admin or owner can delete
        boolean isAdmin = currentUser.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        boolean isOwner = document.getUploadedBy().getId().equals(currentUser.getId());
        if (!isAdmin && !isOwner) {
            throw new AccessDeniedException("You do not have permission to delete this document.");
        }

        String fileName = document.getFileUrl().substring(document.getFileUrl().lastIndexOf('/') + 1);
        fileStorageService.deleteFile(fileName);
        documentRepository.delete(document);
    }

    @Transactional(readOnly = true)
    public long countTotalDocuments() {
        return documentRepository.count();
    }

    @Transactional(readOnly = true)
    public long countDocumentsByCategory(DocumentCategory category) {
        return documentRepository.countByCategory(category);
    }

    @Transactional(readOnly = true)
    public long countDocumentsByAccessLevel(AccessLevel accessLevel) {
        return documentRepository.countByAccessLevel(accessLevel);
    }

    private List<AccessLevel> getAccessibleLevels(String role) {
        if (role.equals("ROLE_ADMIN")) {
            return List.of(AccessLevel.PUBLIC, AccessLevel.MEMBER, AccessLevel.COMMITTEE, AccessLevel.ADMIN);
        } else if (role.equals("ROLE_MEMBER")) { // Assuming a COMMITTEE role might exist
            return List.of(AccessLevel.PUBLIC, AccessLevel.MEMBER);
        }
        return List.of(AccessLevel.PUBLIC);
    }

    private DocumentResponse mapDocumentToResponse(Document document) {
        return DocumentResponse.builder()
                .id(document.getId())
                .title(document.getTitle())
                .description(document.getDescription())
                .category(document.getCategory())
                .accessLevel(document.getAccessLevel())
                .fileType(document.getFileType())
                .fileSize(document.getFileSize())
                .fileUrl(document.getFileUrl())
                .uploadedBy(document.getUploadedBy().getId())
                .uploadedByName(document.getUploadedBy().getFullName())
                .downloadCount(document.getDownloadCount())
                .createdAt(document.getCreatedAt())
                .updatedAt(document.getUpdatedAt())
                .build();
    }
}
