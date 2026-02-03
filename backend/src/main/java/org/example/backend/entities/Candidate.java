package org.example.backend.entities;

import jakarta.persistence.*;
import lombok.*;
import org.example.backend.enums.ContractType;
import org.example.backend.enums.EducationLevel;
import java.time.LocalDate;

@Entity
@DiscriminatorValue("CANDIDATE")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Candidate extends User {

    private LocalDate dateOfBirth;
    private String phone;
    private String city;
    private String country; // Morocco

    // Professional info
    private String currentJobTitle;
    private Integer yearsOfExperience;

    @Enumerated(EnumType.STRING)
    private EducationLevel educationLevel;

    @Column(columnDefinition = "TEXT")
    private String skills; // text for the skills

    private double arabicPercent;
    private double frenchPercent;
    private double englishPercent;

    // Job preferences
    @Enumerated(EnumType.STRING)
    private ContractType preferredContract;

    private String preferredLocation;
    private Boolean openToRemote;

    // Files
    private String cvUrl;
    private String portfolioUrl;
    private String linkedinUrl;
    private String profilePicture;
}
