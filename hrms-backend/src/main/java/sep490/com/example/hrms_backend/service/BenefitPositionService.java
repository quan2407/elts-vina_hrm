package sep490.com.example.hrms_backend.service;

import org.springframework.stereotype.Service;
import sep490.com.example.hrms_backend.dto.benefit.BenefitPositionDTO;
import sep490.com.example.hrms_backend.dto.benefit.BenefitPositionUpdateDTO;

@Service
public interface BenefitPositionService {
    void assignPositionsToBenefit(BenefitPositionDTO request);

    void unassignPositionFromBenefit(Long benefitId, Long positionId);

    void updateFormula(BenefitPositionUpdateDTO request);
}
