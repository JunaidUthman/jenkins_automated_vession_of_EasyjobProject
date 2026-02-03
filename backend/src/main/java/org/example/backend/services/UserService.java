package org.example.backend.services;

import org.example.backend.entities.User;
import org.example.backend.repositories.RoleRepository;
import org.example.backend.repositories.UserRepo;
import org.example.backend.DTO.CandidateProfileDTO;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

import org.example.backend.entities.Candidate;
import org.example.backend.entities.Recruiter;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class UserService {
    private final UserRepo userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleService roleService;

    public UserService(UserRepo userRepository, PasswordEncoder passwordEncoder, RoleRepository roleRepository,
            RoleService roleService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleService = roleService;
    }

    public CandidateProfileDTO getCandidateProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!(user instanceof Candidate)) {
            throw new RuntimeException("User is not a candidate. User type: " + user.getClass().getSimpleName());
        }

        Candidate candidate = (Candidate) user;

        return CandidateProfileDTO.builder()
                .username(candidate.getUsername())
                .email(candidate.getEmail())
                .dateOfBirth(candidate.getDateOfBirth())
                .phone(candidate.getPhone())
                .city(candidate.getCity())
                .country(candidate.getCountry())
                .currentJobTitle(candidate.getCurrentJobTitle())
                .yearsOfExperience(candidate.getYearsOfExperience())
                .educationLevel(candidate.getEducationLevel())
                .skills(candidate.getSkills())
                .arabicPercent(candidate.getArabicPercent())
                .frenchPercent(candidate.getFrenchPercent())
                .englishPercent(candidate.getEnglishPercent())
                .preferredContract(candidate.getPreferredContract())
                .preferredLocation(candidate.getPreferredLocation())
                .openToRemote(candidate.getOpenToRemote())
                .cvUrl(candidate.getCvUrl())
                .portfolioUrl(candidate.getPortfolioUrl())
                .linkedinUrl(candidate.getLinkedinUrl())
                .profilePicture(candidate.getProfilePicture())
                .build();
    }

    public void updateCandidateProfile(String email, org.example.backend.DTO.CandidateProfileDTO dto) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!(user instanceof Candidate)) {
            throw new RuntimeException("Logged in user is not a candidate");
        }

        Candidate candidate = (Candidate) user;

        candidate.setUsername(dto.getUsername());
        candidate.setDateOfBirth(dto.getDateOfBirth());
        candidate.setPhone(dto.getPhone());
        candidate.setCity(dto.getCity());
        candidate.setCountry(dto.getCountry());
        candidate.setCurrentJobTitle(dto.getCurrentJobTitle());
        candidate.setYearsOfExperience(dto.getYearsOfExperience());
        candidate.setEducationLevel(dto.getEducationLevel());
        candidate.setSkills(dto.getSkills());
        candidate.setArabicPercent(dto.getArabicPercent());
        candidate.setFrenchPercent(dto.getFrenchPercent());
        candidate.setEnglishPercent(dto.getEnglishPercent());
        candidate.setPreferredContract(dto.getPreferredContract());
        candidate.setPreferredLocation(dto.getPreferredLocation());
        candidate.setOpenToRemote(dto.getOpenToRemote());
        candidate.setCvUrl(dto.getCvUrl());
        candidate.setPortfolioUrl(dto.getPortfolioUrl());
        candidate.setLinkedinUrl(dto.getLinkedinUrl());
        candidate.setProfilePicture(dto.getProfilePicture());

        userRepository.save(candidate);
    }

    public User registerUser(String username, String email, String rawPassword, String roleName) {

        Optional<User> existingUser = userRepository.findByEmail(email);
        if (existingUser.isPresent()) {
            return null;
        }

        User user;
        if ("Candidate".equalsIgnoreCase(roleName)) {
            user = new Candidate();
        } else if ("Recrutter".equalsIgnoreCase(roleName)) {
            user = new Recruiter();
        } else {
            user = new User();
        }

        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(rawPassword));

        // Save first to have an ID (needed for @ManyToMany join table)
        user = userRepository.save(user);

        // Assign role using your RoleService
        roleService.assignRoleToUser(user, roleName);

        return user;
    }

    public void uploadProfilePicture(String email, MultipartFile file) throws Exception {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!(user instanceof Candidate)) {
            throw new RuntimeException("User is not a candidate");
        }

        Candidate candidate = (Candidate) user;
        String fileName = saveFile(file);
        candidate.setProfilePicture(fileName);
        userRepository.save(candidate);
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
