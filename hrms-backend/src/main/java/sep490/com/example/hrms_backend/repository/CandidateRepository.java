package sep490.com.example.hrms_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sep490.com.example.hrms_backend.entity.Candidate;

import java.util.Optional;

public interface CandidateRepository extends JpaRepository<Candidate, Long> {
    boolean existsByEmail(String email);
    Optional<Candidate> findByPhoneNumber(String phoneNumber);
}
