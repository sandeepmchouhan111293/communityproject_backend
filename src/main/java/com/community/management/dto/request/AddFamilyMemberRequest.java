package com.community.management.dto.request;

import com.community.management.entity.Gender;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
public class AddFamilyMemberRequest {
    @NotBlank
    @Size(max = 255)
    private String name;

    @NotBlank
    @Size(max = 100)
    private String relationship;

    private Integer age;
    private Gender gender;

    @Size(max = 255)
    private String profession;

    private LocalDate dateOfBirth;

    @Size(max = 255)
    private String school;

    private String hobbies;
    private String achievements;

    @Size(max = 50)
    private String maritalStatus;

    @Size(max = 255)
    private String spouseFamily;

    @Size(max = 255)
    private String spouseCity;
    
    private Integer marriageYear;
}
