package org.example.backend.DTO;

import lombok.AllArgsConstructor;
import org.example.backend.entities.Job;

@AllArgsConstructor
public class JobDTO {
    private Long id;
    private String title;
    private String description;
    private String location;
    private String image;
    private String type;
    private String company;
    private String field;
    private String function;
    private String contract_type;
    private String experienceMin;
    private String experienceMax;
    private String educationLevel;

    public JobDTO(Job job) {
        this.id = job.getId();
        this.title = job.getTitle();
        this.description = job.getDescription();
        this.location = job.getLocation();
//        if (job.getImage() != null && !job.getImage().isEmpty()) {
//            this.image = "http://localhost:8080/jobs/image/" + job.getImage();
//        } else {
//            this.image = null; // or some placeholder URL
//        }
        this.type = job.getType().toString();
        this.image= job.getImage();
        this.company = job.getCompany();
        this.field = job.getField();
        this.function = job.getFunction();
        this.contract_type = job.getContract_type();
        this.experienceMin = job.getExperienceMin();
        this.experienceMax = job.getExperienceMax();
        this.educationLevel = job.getEducationLevel();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getFunction() {
        return function;
    }

    public void setFunction(String function) {
        this.function = function;
    }

    public String getContract_type() {
        return contract_type;
    }

    public void setContract_type(String contract_type) {
        this.contract_type = contract_type;
    }

    public String getExperienceMin() {
        return experienceMin;
    }

    public void setExperienceMin(String experienceMin) {
        this.experienceMin = experienceMin;
    }

    public String getExperienceMax() {
        return experienceMax;
    }

    public void setExperienceMax(String experienceMax) {
        this.experienceMax = experienceMax;
    }

    public String getEducationLevel() {
        return educationLevel;
    }

    public void setEducationLevel(String educationLevel) {
        this.educationLevel = educationLevel;
    }
}
