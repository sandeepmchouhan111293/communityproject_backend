package com.community.management.service;

import com.community.management.dto.request.UpdateDirectoryEntryRequest;
import com.community.management.dto.response.DirectoryEntryResponse;
import com.community.management.entity.Directory;
import com.community.management.entity.User;
import com.community.management.exception.ResourceNotFoundException;
import com.community.management.repository.DirectoryRepository;
import com.community.management.repository.UserRepository;
import com.community.management.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class DirectoryService {

    @Autowired
    private DirectoryRepository directoryRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<DirectoryEntryResponse> getAllDirectoryEntries(String displayName, String city, String state, String district, String communityName) {
        List<Directory> entries = directoryRepository.findAll();

        return entries.stream()
                .filter(Directory::isPublic) // Only show public entries by default
                .filter(entry -> displayName == null || entry.getDisplayName().toLowerCase().contains(displayName.toLowerCase()))
                .filter(entry -> city == null || (entry.getUser().getCity() != null && entry.getUser().getCity().toLowerCase().contains(city.toLowerCase())))
                .filter(entry -> state == null || (entry.getUser().getState() != null && entry.getUser().getState().toLowerCase().contains(state.toLowerCase())))
                .filter(entry -> district == null || (entry.getUser().getDistrict() != null && entry.getUser().getDistrict().toLowerCase().contains(district.toLowerCase())))
                .filter(entry -> communityName == null || (entry.getUser().getCommunityName() != null && entry.getUser().getCommunityName().toLowerCase().contains(communityName.toLowerCase())))
                .map(this::mapDirectoryToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public DirectoryEntryResponse getDirectoryEntryById(UUID entryId) {
        Directory directory = directoryRepository.findById(entryId)
                .orElseThrow(() -> new ResourceNotFoundException("DirectoryEntry", "id", entryId));
        
        if (!directory.isPublic()) {
            throw new ResourceNotFoundException("DirectoryEntry", "id", entryId); // Treat as not found if private
        }
        return mapDirectoryToResponse(directory);
    }

    @Transactional(readOnly = true)
    public DirectoryEntryResponse getMyDirectoryEntry(UserPrincipal currentUser) {
        Directory directory = directoryRepository.findByUserId(currentUser.getId())
                .orElseGet(() -> createDefaultDirectoryEntry(currentUser.getId())); // Create if not exists
        return mapDirectoryToResponse(directory);
    }

    @Transactional
    public DirectoryEntryResponse updateMyDirectoryEntry(UpdateDirectoryEntryRequest request, UserPrincipal currentUser) {
        Directory directory = directoryRepository.findByUserId(currentUser.getId())
                .orElseGet(() -> createDefaultDirectoryEntry(currentUser.getId()));

        if (request.getDisplayName() != null) directory.setDisplayName(request.getDisplayName());
        if (request.getContactInfo() != null) directory.setContactInfo(request.getContactInfo());
        if (request.getBio() != null) directory.setBio(request.getBio());
        if (request.getSkills() != null) directory.setSkills(request.getSkills());
        if (request.getInterests() != null) directory.setInterests(request.getInterests());
        if (request.getSocialLinks() != null) directory.setSocialLinks(request.getSocialLinks());
        if (request.getIsPublic() != null) directory.setPublic(request.getIsPublic());

        Directory updatedDirectory = directoryRepository.save(directory);
        return mapDirectoryToResponse(updatedDirectory);
    }

    private Directory createDefaultDirectoryEntry(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        
        Directory newEntry = new Directory();
        newEntry.setUser(user);
        newEntry.setDisplayName(user.getFullName()); // Default display name
        newEntry.setPublic(true); // Default to public
        return directoryRepository.save(newEntry);
    }

    private DirectoryEntryResponse mapDirectoryToResponse(Directory directory) {
        return DirectoryEntryResponse.builder()
                .id(directory.getId())
                .userId(directory.getUser().getId())
                .userName(directory.getUser().getFullName())
                .displayName(directory.getDisplayName())
                .contactInfo(directory.getContactInfo())
                .bio(directory.getBio())
                .skills(directory.getSkills())
                .interests(directory.getInterests())
                .socialLinks(directory.getSocialLinks())
                .isPublic(directory.isPublic())
                .createdAt(directory.getCreatedAt())
                .updatedAt(directory.getUpdatedAt())
                .build();
    }
}