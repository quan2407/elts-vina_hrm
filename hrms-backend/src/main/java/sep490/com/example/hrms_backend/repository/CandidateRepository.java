package sep490.com.example.hrms_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sep490.com.example.hrms_backend.entity.Candidate;

public interface CandidateRepository extends JpaRepository<Candidate, Long> {
}
