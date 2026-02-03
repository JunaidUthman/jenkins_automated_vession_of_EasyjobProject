package org.example.backend.web;

import org.example.backend.DTO.CandidateProfileDTO;
import org.example.backend.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.Map;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    private final UserService userService;

    public ProfileController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<CandidateProfileDTO> getProfile(Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(userService.getCandidateProfile(email));
    }

    @PutMapping
    public ResponseEntity<?> updateProfile(@RequestBody CandidateProfileDTO dto, Authentication authentication) {
        String email = authentication.getName();
        try {
            userService.updateCandidateProfile(email, dto);
            return ResponseEntity.ok(Map.of("msg", "Profile updated successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("msg", "Error updating profile: " + e.getMessage()));
        }
    }

    @PostMapping("/upload-picture")
    public ResponseEntity<?> uploadPicture(@RequestParam("file") MultipartFile file, Authentication authentication) {
        String email = authentication.getName();
        try {
            userService.uploadProfilePicture(email, file);
            return ResponseEntity.ok(Map.of("msg", "Profile picture updated successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("msg", "Error uploading picture: " + e.getMessage()));
        }
    }
}
