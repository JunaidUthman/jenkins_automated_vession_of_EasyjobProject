package org.example.backend;

import io.github.cdimascio.dotenv.Dotenv;
import org.example.backend.entities.Role;
import org.example.backend.repositories.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BackendApplication {

    public static void main(String[] args) {
        Dotenv dotenv = Dotenv.configure()
                .ignoreIfMissing() // Prevent errors if .env file is missing
                .load();

        System.setProperty("DB_URL", dotenv.get("DB_URL"));
        System.setProperty("DB_USERNAME", dotenv.get("DB_USERNAME"));
        System.setProperty("DB_PASSWORD", dotenv.get("DB_PASSWORD"));

        SpringApplication.run(BackendApplication.class, args);
    }

    /**
     * This bean runs once when the application starts.
     * It ensures that required roles exist in the database.
     */
    //@Bean
    CommandLineRunner initRoles(RoleRepository roleRepository) {
        return args -> {
            String[] roles = {"CANDIDATE", "RECRUITER"};

            for (String roleName : roles) {
                roleRepository.findByName(roleName)
                        .orElseGet(() -> {
                            Role role = new Role(roleName);
                            System.out.println("Creating role: " + roleName);
                            return roleRepository.save(role);
                        });
            }
        };
    }
}
