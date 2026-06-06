package sep490.com.example.hrms_backend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sep490.com.example.hrms_backend.dto.RecruitmentGraphResponse;
import sep490.com.example.hrms_backend.entity.Recruitment;

import java.time.LocalDateTime;
import java.util.List;

public interface RecruitmentRepository extends JpaRepository<Recruitment, Long> {
    List<Recruitment> findByTitleContainingIgnoreCase(String keyword);

    @Query("""
            SELECT new sep490.com.example.hrms_backend.dto.RecruitmentGraphResponse(
                    r.id,
                    r.title,
                    r.quantity,
                    COUNT(DISTINCT CASE WHEN i.result = sep490.com.example.hrms_backend.enums.InterviewResult.PASS THEN i.candidate.id END),
                    COUNT(DISTINCT cr.id)
                )
                FROM Recruitment r
                LEFT JOIN CandidateRecruitment cr ON cr.recruitment.id = r.id
                LEFT JOIN InterviewSchedule i ON i.recruitment.id = r.id AND i.result IS NOT NULL
                WHERE r.createAt BETWEEN :fromDate AND :toDate
                GROUP BY r.id, r.title, r.quantity
            """)
    List<RecruitmentGraphResponse> getRecruitmentGraphData(@Param("fromDate") LocalDateTime fromDate, @Param("toDate") LocalDateTime toDate);
    Page<Recruitment> findByTitleContainingIgnoreCase(String title, Pageable pageable);
}
