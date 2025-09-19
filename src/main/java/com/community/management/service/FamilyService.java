package com.community.management.service;

import com.community.management.dto.request.AddFamilyMemberRequest;
import com.community.management.dto.request.UpdateFamilyMemberRequest;
import com.community.management.dto.response.FamilyMemberResponse;
import com.community.management.entity.FamilyMember;
import com.community.management.entity.User;
import com.community.management.exception.ResourceNotFoundException;
import com.community.management.repository.FamilyMemberRepository;
import com.community.management.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class FamilyService {

    @Autowired
    private FamilyMemberRepository familyMemberRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public FamilyMemberResponse addMember(UUID userId, AddFamilyMemberRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        FamilyMember member = new FamilyMember();
        member.setUser(user);
        mapRequestToFamilyMember(request, member);

        FamilyMember savedMember = familyMemberRepository.save(member);
        return mapFamilyMemberToResponse(savedMember);
    }

    @Transactional(readOnly = true)
    public List<FamilyMemberResponse> getMembers(UUID userId) {
        return familyMemberRepository.findByUserId(userId).stream()
                .map(this::mapFamilyMemberToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public FamilyMemberResponse getMemberById(UUID memberId, UUID userId) {
        FamilyMember member = familyMemberRepository.findById(memberId)
                .filter(m -> m.getUser().getId().equals(userId))
                .orElseThrow(() -> new ResourceNotFoundException("FamilyMember", "id", memberId));
        return mapFamilyMemberToResponse(member);
    }

    @Transactional
    public FamilyMemberResponse updateMember(UUID memberId, UUID userId, UpdateFamilyMemberRequest request) {
        FamilyMember member = familyMemberRepository.findById(memberId)
                .filter(m -> m.getUser().getId().equals(userId))
                .orElseThrow(() -> new ResourceNotFoundException("FamilyMember", "id", memberId));
        
        mapRequestToFamilyMember(request, member);

        FamilyMember updatedMember = familyMemberRepository.save(member);
        return mapFamilyMemberToResponse(updatedMember);
    }

    @Transactional
    public void deleteMember(UUID memberId, UUID userId) {
        FamilyMember member = familyMemberRepository.findById(memberId)
                .filter(m -> m.getUser().getId().equals(userId))
                .orElseThrow(() -> new ResourceNotFoundException("FamilyMember", "id", memberId));
        familyMemberRepository.delete(member);
    }

    private FamilyMemberResponse mapFamilyMemberToResponse(FamilyMember member) {
        return FamilyMemberResponse.builder()
                .id(member.getId())
                .name(member.getName())
                .relationship(member.getRelationship())
                .age(member.getAge())
                .gender(member.getGender())
                .profession(member.getProfession())
                .dateOfBirth(member.getDateOfBirth())
                .school(member.getSchool())
                .hobbies(member.getHobbies())
                .achievements(member.getAchievements())
                .maritalStatus(member.getMaritalStatus())
                .spouseFamily(member.getSpouseFamily())
                .spouseCity(member.getSpouseCity())
                .marriageYear(member.getMarriageYear())
                .build();
    }

    private void mapRequestToFamilyMember(AddFamilyMemberRequest request, FamilyMember member) {
        member.setName(request.getName());
        member.setRelationship(request.getRelationship());
        member.setAge(request.getAge());
        member.setGender(request.getGender());
        member.setProfession(request.getProfession());
        member.setDateOfBirth(request.getDateOfBirth());
        member.setSchool(request.getSchool());
        member.setHobbies(request.getHobbies());
        member.setAchievements(request.getAchievements());
        member.setMaritalStatus(request.getMaritalStatus());
        member.setSpouseFamily(request.getSpouseFamily());
        member.setSpouseCity(request.getSpouseCity());
        member.setMarriageYear(request.getMarriageYear());
    }
    
    private void mapRequestToFamilyMember(UpdateFamilyMemberRequest request, FamilyMember member) {
        if (request.getName() != null) member.setName(request.getName());
        if (request.getRelationship() != null) member.setRelationship(request.getRelationship());
        if (request.getAge() != null) member.setAge(request.getAge());
        if (request.getGender() != null) member.setGender(request.getGender());
        if (request.getProfession() != null) member.setProfession(request.getProfession());
        if (request.getDateOfBirth() != null) member.setDateOfBirth(request.getDateOfBirth());
        if (request.getSchool() != null) member.setSchool(request.getSchool());
        if (request.getHobbies() != null) member.setHobbies(request.getHobbies());
        if (request.getAchievements() != null) member.setAchievements(request.getAchievements());
        if (request.getMaritalStatus() != null) member.setMaritalStatus(request.getMaritalStatus());
        if (request.getSpouseFamily() != null) member.setSpouseFamily(request.getSpouseFamily());
        if (request.getSpouseCity() != null) member.setSpouseCity(request.getSpouseCity());
        if (request.getMarriageYear() != null) member.setMarriageYear(request.getMarriageYear());
    }
}
