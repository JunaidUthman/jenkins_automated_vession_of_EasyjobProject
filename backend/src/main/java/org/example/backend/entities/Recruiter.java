package org.example.backend.entities;

import jakarta.persistence.*;
import lombok.*;
import org.example.backend.enums.*;

@Entity
@DiscriminatorValue("RECRUITER")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Recruiter extends User {

    private String companyName;

    @Column(columnDefinition = "TEXT")
    private String companyDescription;

    private String companyWebsite;
    private String companyLogo;

    // Recruiter info
    private String phone;

    @Enumerated(EnumType.STRING)
    private RecruiterPosition position;

    // Company details
    @Enumerated(EnumType.STRING)
    private Industry industry;

    @Enumerated(EnumType.STRING)
    private CompanySize companySize;

    @Enumerated(EnumType.STRING)
    private City city;

    @Enumerated(EnumType.STRING)
    private Country country;
}
