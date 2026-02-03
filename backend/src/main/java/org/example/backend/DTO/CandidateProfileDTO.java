package org.example.backend.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.backend.enums.ContractType;
import org.example.backend.enums.EducationLevel;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CandidateProfileDTO {
    private String username;
    private String email;
    private LocalDate dateOfBirth;
    private String phone;
    private String city;
    private String country;

    // Professional info
    private String currentJobTitle;
    private Integer yearsOfExperience;
    private EducationLevel educationLevel;
    private String skills;

    private double arabicPercent;
    private double frenchPercent;
    private double englishPercent;

    // Job preferences
    private ContractType preferredContract;
    private String preferredLocation;
    private Boolean openToRemote;

    // Files
    private String cvUrl;
    private String portfolioUrl;
    private String linkedinUrl;
    private String profilePicture;
}
