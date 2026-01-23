package org.example.backend.services;


import lombok.AllArgsConstructor;
import org.example.backend.DTO.JobDTO;
import org.example.backend.entities.Job;
import org.example.backend.entities.User;
import org.example.backend.enums.JobType;
import org.example.backend.repositories.JobRepo;
import org.example.backend.repositories.UserRepo;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class JobService {

    private final JobRepo jobRepository;
    private final UserRepo userRepository;

    public List<JobDTO> getAllJobs() {
        List<JobDTO> jobs = jobRepository.findAll()
                .stream()
                .map(JobDTO::new)
                .toList();

        return jobs;
    }

    public List<JobDTO> getJobsByUserEmail(String email) {
        User creator = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return creator.getCreatedJobs()
                .stream()
                .map(JobDTO::new)
                .toList();
    }

    public Map<String, String> applyJob(String email, Long jobId) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));

        user.getJobs().add(job);
        userRepository.save(user);

        return Map.of("message", "Job applied");
    }

    public List<JobDTO> getApplications(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return user.getJobs()
                .stream()
                .map(JobDTO::new)
                .toList();
    }

    public Map<String, String> cancelApplication(String email, Long jobId) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));

        user.getJobs().remove(job);
        userRepository.save(user);

        return Map.of("message", "Application cancelled successfully");
    }

    public JobDTO createJob(String title, String description, String location, JobType type, MultipartFile image, String creatorEmail, String company, String field, String function, String contract_type, String experienceMin, String experienceMax, String educationLevel) throws Exception {
        // Find the creator user
        User creator = userRepository.findByEmail(creatorEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String imageName = null;
        if (image != null && !image.isEmpty()) {
            // Generate unique filename
            imageName = System.currentTimeMillis() + "_" + image.getOriginalFilename();
            String uploadDir = "static/uploads/images/"; // choose your folder
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            Path filePath = uploadPath.resolve(imageName);
            Files.write(filePath, image.getBytes());
        }

        // Create new job
        Job job = new Job();
        job.setTitle(title);
        job.setDescription(description);
        job.setLocation(location);
        job.setImage(imageName);
        job.setType(type);
        job.setCreator(creator);
        job.setCompany(company);
        job.setField(field);
        job.setFunction(function);
        job.setContract_type(contract_type);
        job.setExperienceMin(experienceMin);
        job.setExperienceMax(experienceMax);
        job.setEducationLevel(educationLevel);

        // Save to database
        Job savedJob = jobRepository.save(job);

        return new JobDTO(savedJob);
    }

}
