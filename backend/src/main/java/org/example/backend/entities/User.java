package org.example.backend.entities;

import jakarta.persistence.*;
import java.util.*;

@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "user_type", discriminatorType = DiscriminatorType.STRING)
@DiscriminatorValue("USER")
public class User {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        private String username;
        private String email;
        private String password;

        // Many-to-Many with Role
        @ManyToMany(fetch = FetchType.EAGER)
        @JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
        private Set<Role> roles = new HashSet<>();

        // Many-to-Many with Job (apply jobs)
        @ManyToMany
        @JoinTable(name = "user_jobs", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "job_id"))
        private Set<Job> jobs = new HashSet<>();

        // 🔹 One-to-Many with Notification
        @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
        private List<Notification> notifications;

        // 🔹 One-to-Many: User creates many jobs
        @OneToMany(mappedBy = "creator", cascade = CascadeType.ALL, orphanRemoval = true)
        private List<Job> createdJobs = new ArrayList<>();

        public User() {
        }

        // Getters and setters
        public Long getId() {
                return id;
        }

        public void setId(Long id) {
                this.id = id;
        }

        public String getUsername() {
                return username;
        }

        public void setUsername(String username) {
                this.username = username;
        }

        public String getEmail() {
                return email;
        }

        public void setEmail(String email) {
                this.email = email;
        }

        public String getPassword() {
                return password;
        }

        public void setPassword(String password) {
                this.password = password;
        }

        public Set<Role> getRoles() {
                return roles;
        }

        public void setRoles(Set<Role> roles) {
                this.roles = roles;
        }

        public Set<Job> getJobs() {
                return jobs;
        }

        public void setJobs(Set<Job> jobs) {
                this.jobs = jobs;
        }

        public List<Notification> getNotifications() {
                return notifications;
        }

        public void setNotifications(List<Notification> notifications) {
                this.notifications = notifications;
        }

        public List<Job> getCreatedJobs() {
                return createdJobs;
        }

        public void setCreatedJobs(List<Job> createdJobs) {
                this.createdJobs = createdJobs;
        }
}
