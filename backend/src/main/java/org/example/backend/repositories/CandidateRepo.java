package org.example.backend.repositories;

import org.example.backend.entities.Candidate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CandidateRepo extends JpaRepository<Candidate, Long> {
    Optional<Candidate> findByEmail(String email);
}
