package sep490.com.example.hrms_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sep490.com.example.hrms_backend.entity.Benefit;
import sep490.com.example.hrms_backend.entity.BenefitPosition;
import sep490.com.example.hrms_backend.entity.BenefitRegistration;
import sep490.com.example.hrms_backend.entity.Position;

import java.util.List;
import java.util.Optional;

public interface BenefitPositionRepository  extends JpaRepository<BenefitPosition, Long> {
    @Query("SELECT bp.position.positionId FROM BenefitPosition bp WHERE bp.benefit.id = :benefitId")
    List<Long> findPositionIdsByBenefitId(@Param("benefitId") Long benefitId);

    boolean existsByBenefitAndPosition(Benefit benefit, Position position);

    void deleteByBenefitAndPosition(Benefit benefit, Position position);

    Optional<BenefitPosition> findByBenefit_IdAndPosition_PositionId(Long benefitId, Long positionId);
}
