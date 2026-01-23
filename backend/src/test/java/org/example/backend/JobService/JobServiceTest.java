package org.example.backend.JobService;


import org.example.backend.DTO.JobDTO;
import org.example.backend.entities.Job;
import org.example.backend.entities.User;
import org.example.backend.enums.JobType;
import org.example.backend.repositories.JobRepo;
import org.example.backend.repositories.UserRepo;
import org.example.backend.services.JobService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class) // Enables Mockito in JUnit 5
public class JobServiceTest {

    @Mock // we used this to simulate the behaviour of jobRepo , because we dont wanna touch the database for reel, we just wanna test
    private JobRepo jobRepo;

    @Mock
    private UserRepo userRepo;

    @InjectMocks // this is the class underTest, when we use @InjectMocks, springboot inject the serices we mocked automaticly in here
    private JobService jobService;

    private Long id;
    private String title;
    private String description;
    private String location;
    private String image;
    private String type;

    @Test
    public void shouldReturnAllJobsAsDTOs() {
        // 1️⃣ Arrange - define fake data
        Job job1 = new Job(1L, "Java Developer", "Backend job","test","test", JobType.INTERNSHIP, null, null, null, null, null, null, null);
        Job job2 = new Job(1L, "test", "Backend job","test","test",JobType.INTERNSHIP, null, null, null, null, null, null, null);

        when(jobRepo.findAll()).thenReturn(List.of(job1, job2));// simulate what shoul happen when teh service calls the jobRepo

        // Act - call the method we want to test
        List<JobDTO> result = jobService.getAllJobs();

        // 3️⃣ Assert - verify the result
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getTitle()).isEqualTo("Java Developer");
        assertThat(result.get(1).getTitle()).isEqualTo("test");
    }

    @Test
    public void getJobsByUserEmailShouldReturnJobs(){
        Job job1 = new Job(1L, "Java Developer", "Backend job","test","test", JobType.INTERNSHIP, null, null, null, null, null, null, null);
        Job job2 = new Job(1L, "test", "Backend job","test","test",JobType.INTERNSHIP, null, null, null, null, null, null, null);

        User user = new User();

        user.setEmail("test@test");
        user.getCreatedJobs().addAll(List.of(job1,job2));

        when(userRepo.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        List<JobDTO> jobs =  jobService.getJobsByUserEmail(user.getEmail());

        assertThat(jobs).hasSize(2);
        assertThat(jobs.get(0).getTitle()).isEqualTo("Java Developer");


    }

    @Test
    void shouldThrowExceptionIfEmailNotFound() {
        // Arrange: mock repository to return empty
        when(userRepo.findByEmail("unknown@example.com"))
                .thenReturn(Optional.empty());

        // Act & Assert: check exception
        assertThatThrownBy(() -> jobService.getJobsByUserEmail("unknown@example.com"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("User not found");
    }

    @Test
    void applyJobShouldReturnSuccessMessage() {
        // Arrange
        User user = new User();
        user.setEmail("test@test");
        Job job = new Job(1L, "Java Developer", "Backend job", "test", "test", JobType.INTERNSHIP, null, null, null, null, null, null, null);

        when(userRepo.findByEmail("test@test")).thenReturn(Optional.of(user));
        when(jobRepo.findById(1L)).thenReturn(Optional.of(job));

        // Act
        Map<String, String> result = jobService.applyJob("test@test", 1L);

        // Assert
        assertThat(result).containsEntry("message", "Job applied");
        verify(userRepo).save(user);
    }

    @Test
    void applyJobShouldThrowExceptionIfUserNotFound() {
        // Arrange
        when(userRepo.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> jobService.applyJob("unknown@example.com", 1L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("User not found");
    }

    @Test
    void applyJobShouldThrowExceptionIfJobNotFound() {
        // Arrange
        User user = new User();
        user.setEmail("test@test");

        when(userRepo.findByEmail("test@test")).thenReturn(Optional.of(user));
        when(jobRepo.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> jobService.applyJob("test@test", 1L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Job not found");
    }

    @Test
    void getApplicationsShouldReturnJobs() {
        Job job1 = new Job(1L, "Python Developer", "Backend job", "test", "test", JobType.INTERNSHIP, null, null, null, null, null, null, null);
        Job job2 = new Job(2L, "Python Developer", "Backend job", "test", "test", JobType.INTERNSHIP, null, null, null, null, null, null, null);

        User user = new User();
        user.setEmail("test@test");
        user.getJobs().addAll(List.of(job1, job2));

        when(userRepo.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        List<JobDTO> applications = jobService.getApplications(user.getEmail());

        assertThat(applications).hasSize(2);
        assertThat(applications.get(0).getTitle()).isEqualTo("Python Developer");
        assertThat(applications.get(1).getTitle()).isEqualTo("Python Developer");
    }

    @Test
    void getApplicationsShouldThrowExceptionIfUserNotFound() {
        when(userRepo.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> jobService.getApplications("unknown@example.com"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("User not found");
    }

    @Test
    void cancelApplicationShouldReturnSuccessMessage() {
        // Arrange
        User user = new User();
        user.setEmail("test@test");
        Job job = new Job(1L, "Java Developer", "Backend job", "test", "test", JobType.INTERNSHIP, null, null, null, null, null, null, null);
        user.getJobs().add(job);

        when(userRepo.findByEmail("test@test")).thenReturn(Optional.of(user));
        when(jobRepo.findById(1L)).thenReturn(Optional.of(job));

        // Act
        Map<String, String> result = jobService.cancelApplication("test@test", 1L);

        // Assert
        assertThat(result).containsEntry("message", "Application cancelled successfully");
        // This checks that save() was called once with the exact user object
        verify(userRepo).save(user);
    }

    @Test
    void cancelApplicationShouldThrowExceptionIfUserNotFound() {
        when(userRepo.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> jobService.cancelApplication("unknown@example.com", 1L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("User not found");
    }

    @Test
    void cancelApplicationShouldThrowExceptionIfJobNotFound() {
        User user = new User();
        user.setEmail("test@test");

        when(userRepo.findByEmail("test@test")).thenReturn(Optional.of(user));
        when(jobRepo.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> jobService.cancelApplication("test@test", 1L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Job not found");
    }

    @Test
    void createJobShouldReturnJobDTO() throws Exception {
        // Arrange
        User creator = new User();
        creator.setEmail("creator@test");

        MockMultipartFile image = new MockMultipartFile("image", "test.jpg", "image/jpeg", "test image content".getBytes());

        Job savedJob = new Job(1L, "Java Developer", "Backend job", "test", "123456789_test.jpg", JobType.INTERNSHIP, null, null, null, null, null, null, null);
        savedJob.setCreator(creator);

        when(userRepo.findByEmail("creator@test")).thenReturn(Optional.of(creator));
        when(jobRepo.save(any(Job.class))).thenReturn(savedJob); // we add any(Job.class)) cause savedJob is not necesserly the same thing that is gonne get returned, an id might get added up so we pass a job class but without specifing what gonna be in it

        // Act
        JobDTO result = jobService.createJob("Java Developer", "Backend job", "test", JobType.INTERNSHIP, image, "creator@test", "Test Company", "IT", "Developer", "Full-time", "1", "5", "Bachelor");

        // Assert
        assertThat(result.getTitle()).isEqualTo("Java Developer");
        assertThat(result.getDescription()).isEqualTo("Backend job");
        verify(jobRepo).save(any(Job.class));
    }

    @Test
    void createJobShouldThrowExceptionIfUserNotFound() {
        MockMultipartFile image = new MockMultipartFile("image", "test.jpg", "image/jpeg", "test image content".getBytes());

        when(userRepo.findByEmail("unknown@test")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> jobService.createJob("Java Developer", "Backend job", "test", JobType.INTERNSHIP, image, "unknown@test", "Test Company", "IT", "Developer", "Full-time", "1", "5", "Bachelor"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("User not found");
    }

    @Test
    void createJobShouldHandleNullImage() throws Exception {
        // Arrange
        User creator = new User();
        creator.setEmail("creator@test");

        Job savedJob = new Job(1L, "Java Developer", "Backend job", "test", null, JobType.INTERNSHIP, null, null, null, null, null, null, null);
        savedJob.setCreator(creator);

        when(userRepo.findByEmail("creator@test")).thenReturn(Optional.of(creator));
        when(jobRepo.save(any(Job.class))).thenReturn(savedJob);

        // Act
        JobDTO result = jobService.createJob("Java Developer", "Backend job", "test", JobType.INTERNSHIP, null, "creator@test", "Test Company", "IT", "Developer", "Full-time", "1", "5", "Bachelor");

        // Assert
        assertThat(result.getTitle()).isEqualTo("Java Developer");
        assertThat(result.getImage()).isNull();
        verify(jobRepo).save(any(Job.class));
    }

}
