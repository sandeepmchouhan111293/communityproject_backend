package com.community.management.dto.response;

import com.community.management.entity.Gender;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
public class FamilyMemberResponse {
    private UUID id;
    private String name;
    private String relationship;
    private Integer age;
    private Gender gender;
    private String profession;
    private LocalDate dateOfBirth;
    private String school;
    private String hobbies;
    private String achievements;
    private String maritalStatus;
    private String spouseFamily;
    private String spouseCity;
    private Integer marriageYear;
}
