package org.example.backend.services;

import lombok.AllArgsConstructor;
import org.example.backend.DTO.JobDTO;
import org.example.backend.entities.Job;
import org.example.backend.entities.User;
import org.example.backend.enums.ContractType;
import org.example.backend.enums.EducationLevel;
import org.example.backend.enums.JobType;
import org.example.backend.repositories.JobRepo;
import org.example.backend.repositories.UserRepo;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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

                // Check if user has already applied
                if (userRepository.hasAppliedToJob(user.getId(), jobId)) {
                        return Map.of("message", "You have already applied to this job", "alreadyApplied", "true");
                }

                user.getJobs().add(job);
                userRepository.save(user);

                return Map.of("message", "Application submitted successfully", "success", "true");
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

        public JobDTO createJob(String title, String description, String location, JobType type,
                        MultipartFile imageLogo, String creatorEmail, String company, String field, String function,
                        String contract_type, String experienceMin, String experienceMax, String educationLevel)
                        throws Exception {
                // Find the creator user
                User creator = userRepository.findByEmail(creatorEmail)
                                .orElseThrow(() -> new RuntimeException("User not found"));

                String logoName = null;
                if (imageLogo != null && !imageLogo.isEmpty()) {
                        logoName = saveFile(imageLogo);
                }

                // Create new job
                Job job = new Job();
                job.setTitle(title);
                job.setDescription(description);
                job.setLocation(location);
                job.setImage(null); // 🔹 Background image removed
                job.setCompanyLogo(logoName);
                job.setType(type);
                job.setCreator(creator);
                job.setCompany(company);
                job.setField(field);
                job.setJobFunction(function);

                if (contract_type != null && !contract_type.isEmpty()) {
                        job.setContract_type(ContractType.valueOf(contract_type));
                }

                job.setExperienceMin(experienceMin);
                job.setExperienceMax(experienceMax);

                if (educationLevel != null && !educationLevel.isEmpty()) {
                        job.setEducationLevel(EducationLevel.valueOf(educationLevel));
                }

                // Save to database
                Job savedJob = jobRepository.save(job);

                return new JobDTO(savedJob);
        }

        public Map<String, String> deleteJob(Long jobId) {
                Job job = jobRepository.findById(jobId)
                                .orElseThrow(() -> new RuntimeException("Job not found"));

                jobRepository.delete(job);
                return Map.of("message", "Job deleted successfully");
        }

        public JobDTO updateJob(Long jobId, String title, String description, String location, JobType type,
                        MultipartFile imageLogo, String company, String field, String function,
                        String contract_type, String experienceMin, String experienceMax, String educationLevel)
                        throws Exception {

                Job job = jobRepository.findById(jobId)
                                .orElseThrow(() -> new RuntimeException("Job not found"));

                job.setTitle(title);
                job.setDescription(description);
                job.setLocation(location);
                job.setType(type);
                job.setCompany(company);
                job.setField(field);
                job.setJobFunction(function);

                if (imageLogo != null && !imageLogo.isEmpty()) {
                        String logoName = saveFile(imageLogo);
                        job.setCompanyLogo(logoName);
                }

                if (contract_type != null && !contract_type.isEmpty()) {
                        job.setContract_type(ContractType.valueOf(contract_type));
                }

                job.setExperienceMin(experienceMin);
                job.setExperienceMax(experienceMax);

                if (educationLevel != null && !educationLevel.isEmpty()) {
                        job.setEducationLevel(EducationLevel.valueOf(educationLevel));
                }

                Job updatedJob = jobRepository.save(job);
                return new JobDTO(updatedJob);
        }

        private String saveFile(MultipartFile file) throws Exception {
                String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
                String uploadDir = "static/uploads/images/";
                Path uploadPath = Paths.get(uploadDir);
                if (!Files.exists(uploadPath)) {
                        Files.createDirectories(uploadPath);
                }
                Path filePath = uploadPath.resolve(fileName);
                Files.write(filePath, file.getBytes());
                return fileName;
        }

}
