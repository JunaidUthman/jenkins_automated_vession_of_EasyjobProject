package org.example.backend.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.backend.enums.JobType;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "jobs")
@Getter
@Setter
@NoArgsConstructor

public class Job {

//id of the job
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;
    private String location;
    private String image;
    private String company;
    private String field;
    private String function;
    private String contract_type;
    private String experienceMin;
    private String experienceMax;
    private String educationLevel;
    @Enumerated(EnumType.STRING)
    private JobType type;


    public Job(Long id, String title, String description, String location, String image, JobType type, String company, String field, String function, String contract_type, String experienceMin, String experienceMax, String educationLevel) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.location = location;
        this.image = image;
        this.type = type;
        this.company = company;
        this.field = field;
        this.function = function;
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
                ", image='" + image + '\'' +
                ", type=" + type +
                ", creator=" + creator +
                ", users=" + users +
                '}';
    }
}
