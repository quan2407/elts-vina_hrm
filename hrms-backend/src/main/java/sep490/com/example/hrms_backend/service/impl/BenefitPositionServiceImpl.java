package sep490.com.example.hrms_backend.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sep490.com.example.hrms_backend.dto.benefit.BenefitPositionDTO;
import sep490.com.example.hrms_backend.dto.benefit.BenefitPositionUpdateDTO;
import sep490.com.example.hrms_backend.entity.Benefit;
import sep490.com.example.hrms_backend.entity.BenefitPosition;
import sep490.com.example.hrms_backend.entity.Position;
import sep490.com.example.hrms_backend.repository.BenefitPositionRepository;
import sep490.com.example.hrms_backend.repository.BenefitRepository;
import sep490.com.example.hrms_backend.repository.PositionRepository;
import sep490.com.example.hrms_backend.service.BenefitPositionService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BenefitPositionServiceImpl implements BenefitPositionService {

    private final BenefitRepository benefitRepository;
    private final PositionRepository positionRepository;
    private final BenefitPositionRepository benefitPositionRepository;


    @Transactional
    @Override
    public void assignPositionsToBenefit(BenefitPositionDTO request) {
        Benefit benefit = benefitRepository.findById(request.getBenefitId())
                .orElseThrow(() -> new RuntimeException("Benefit not found"));

        List<Position> positions = positionRepository.findAllById(request.getPositionIds());



        List<BenefitPosition> benefitPositions = positions.stream()
                .filter(position -> !benefitPositionRepository.existsByBenefitAndPosition(benefit, position))
                .map(position -> {
                    BenefitPosition bp = new BenefitPosition();
                    bp.setBenefit(benefit);
                    bp.setPosition(position);
                    // Gán giá trị mặc định từ Benefit
                    bp.setFormulaValue(benefit.getDefaultFormulaValue());
                    bp.setFormulaType(benefit.getDefaultFormulaType());
                    return bp;

                })
                .toList();

        benefitPositionRepository.saveAll(benefitPositions);
    }

    @Transactional
    @Override
    public void unassignPositionFromBenefit(Long benefitId, Long positionId) {
        Benefit benefit = benefitRepository.findById(benefitId)
                .orElseThrow(() -> new RuntimeException("Benefit not found"));

        Position position = positionRepository.findById(positionId)
                .orElseThrow(() -> new RuntimeException("Position not found"));

        if (!benefitPositionRepository.existsByBenefitAndPosition(benefit, position)) {
            throw new RuntimeException("This position is not assigned to the benefit.");
        }

        benefitPositionRepository.deleteByBenefitAndPosition(benefit, position);
    }

    @Override
    public void updateFormula(BenefitPositionUpdateDTO request) {
        System.out.println("Update formula" + request.getBenefitId()+ "," +request.getPositionId());
        BenefitPosition bp = benefitPositionRepository
                .findByBenefit_IdAndPosition_PositionId(request.getBenefitId(), request.getPositionId())
                .orElseThrow(() -> new RuntimeException("Benefit-Position not found"));

        bp.setFormulaType(request.getFormulaType());
        bp.setFormulaValue(request.getFormulaValue());

        benefitPositionRepository.save(bp);
    }
}
