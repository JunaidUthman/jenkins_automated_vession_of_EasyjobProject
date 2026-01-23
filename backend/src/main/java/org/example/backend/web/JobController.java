package org.example.backend.web;

import org.example.backend.DTO.JobDTO;
import org.example.backend.enums.JobType;
import org.example.backend.security.AuthRequest;
import org.example.backend.services.JobService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;

import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@RestController
@RequestMapping("/api/jobs")

//TODO :: update routes
public class JobController {

    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    @GetMapping("getAllJobs")
    public ResponseEntity<List<JobDTO>> getAllJobs() {


        List<JobDTO> jobs = jobService.getAllJobs();

        return ResponseEntity.ok(jobs);
    }

    // get jobs by the user authenticated
    @GetMapping("/getJobs")
    public ResponseEntity<List<JobDTO>> getJobsById() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();

            List<JobDTO> jobDTOs = jobService.getJobsByUserEmail(email);

            System.out.println("Returning jobs count: " + jobDTOs.size());
            return ResponseEntity.ok(jobDTOs);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/images/{filename}")
    public ResponseEntity<Resource> getImage(@PathVariable String filename) throws MalformedURLException {
        Path path = Paths.get("static/uploads/images").resolve(filename);
        Resource resource = new UrlResource(path.toUri());
        if (resource.exists() || resource.isReadable()) {
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG) // or dynamically detect
                    .body(resource);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/apply/{jobId}")
    public ResponseEntity<Map<String, String>> applyJob(@PathVariable Long jobId) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        Map<String, String> response = jobService.applyJob(email, jobId);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/getApplications")
    public ResponseEntity<List<JobDTO>> getApplications() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        List<JobDTO> jobsApplied = jobService.getApplications(email);

        return ResponseEntity.ok(jobsApplied);
    }

    @DeleteMapping("/cancelApplication/{jobId}")
    public ResponseEntity<Map<String, String>> cancelApplication(@PathVariable Long jobId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        Map<String, String> response = jobService.cancelApplication(email, jobId);

        return ResponseEntity.ok(response);
    }




    @PostMapping("/createJob")
    public ResponseEntity<JobDTO> createJob(
            @RequestParam String title,
            @RequestParam String description,
            @RequestParam String location,
            @RequestParam(required = true) JobType type,
            @RequestParam(required = false) MultipartFile image,
            @RequestParam String company,
            @RequestParam String field,
            @RequestParam String function,
            @RequestParam String contract_type,
            @RequestParam String experienceMin,
            @RequestParam String experienceMax,
            @RequestParam String educationLevel
    ) {
        try {
            // Get authenticated user email
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();

            JobDTO jobDTO = jobService.createJob(title, description, location, type, image, email, company, field, function, contract_type, experienceMin, experienceMax, educationLevel);

            // Return saved job with CREATED status
            return ResponseEntity.status(HttpStatus.CREATED).body(jobDTO);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
