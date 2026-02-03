package org.example.backend.repositories;

import org.example.backend.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepo extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email); // we use optional to return a user , but if its null , its ok

    @Query("SELECT CASE WHEN COUNT(j) > 0 THEN true ELSE false END FROM User u JOIN u.jobs j WHERE u.id = :userId AND j.id = :jobId")
    boolean hasAppliedToJob(@Param("userId") Long userId, @Param("jobId") Long jobId);
}
