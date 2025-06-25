package sep490.com.example.hrms_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sep490.com.example.hrms_backend.entity.CandidateRecruitment;

import java.util.List;

public interface CandidateRecruitmentRepository  extends JpaRepository<CandidateRecruitment, Long> {
    List<CandidateRecruitment> findByRecruitmentId(Long recruitmentId);
}
