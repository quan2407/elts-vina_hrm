package sep490.com.example.hrms_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sep490.com.example.hrms_backend.entity.CandidateRecruitment;

import java.util.List;
import java.util.Optional;

public interface CandidateRecruitmentRepository  extends JpaRepository<CandidateRecruitment, Long> {
    List<CandidateRecruitment> findByRecruitmentId(Long recruitmentId);

    Optional<CandidateRecruitment> findByCandidateIdAndRecruitmentId(Long candidateId, Long recruitmentId);

    Optional<CandidateRecruitment> findById(Long id);
}
