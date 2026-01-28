package org.example.backend.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.backend.enums.ContractType;
import org.example.backend.enums.EducationLevel;
import org.example.backend.enums.JobType;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "jobs")
@Getter
@Setter
@NoArgsConstructor

public class Job {

    // id of the job
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;
    private String location;
    private String image;
    private String company;
    private String companyLogo; // 🔹 New field for company logo
    private String field;
    private String jobFunction;

    @Enumerated(EnumType.STRING)
    private ContractType contract_type;

    private String experienceMin;
    private String experienceMax;

    @Enumerated(EnumType.STRING)
    private EducationLevel educationLevel;

    @Enumerated(EnumType.STRING)
    private JobType type;

    @Column(name = "created_at", nullable = true, updatable = false)
    private java.time.LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = java.time.LocalDateTime.now();
    }

    public Job(Long id, String title, String description, String location, String image, JobType type, String company,
            String companyLogo, String field, String jobFunction, ContractType contract_type, String experienceMin,
            String experienceMax, EducationLevel educationLevel) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.location = location;
        this.image = image;
        this.type = type;
        this.company = company;
        this.companyLogo = companyLogo;
        this.field = field;
        this.jobFunction = jobFunction;
        this.contract_type = contract_type;
        this.experienceMin = experienceMin;
        this.experienceMax = experienceMax;
        this.educationLevel = educationLevel;
    }

    // 🔹 Job created by a User (the recruiter)
    @ManyToOne
    @JoinColumn(name = "creator_id")
    private User creator;

    // 🔹 Users who applied
    @ManyToMany(mappedBy = "jobs")
    private Set<User> users = new HashSet<>();

    @Override
    public String toString() {
        return "Job{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", location='" + location + '\'' +
                ", createdAt=" + createdAt +
                ", type=" + type +
                ", creator=" + creator +
                ", users=" + users +
                '}';
    }
}
