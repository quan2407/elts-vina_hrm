package sep490.com.example.hrms_backend.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sep490.com.example.hrms_backend.dto.benefit.BenefitPositionDTO;
import sep490.com.example.hrms_backend.dto.benefit.BenefitPositionUpdateDTO;
import sep490.com.example.hrms_backend.entity.*;
import sep490.com.example.hrms_backend.enums.BenefitType;
import sep490.com.example.hrms_backend.enums.FormulaType;
import sep490.com.example.hrms_backend.repository.*;
import sep490.com.example.hrms_backend.service.BenefitPositionService;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BenefitPositionServiceImpl implements BenefitPositionService {

    private final BenefitRepository benefitRepository;
    private final PositionRepository positionRepository;
    private final BenefitPositionRepository benefitPositionRepository;
    private final BenefitRegistrationRepository benefitRegistrationRepository;
    private final EmployeeRepository employeeRepository;

    @Transactional
    @Override
    public void assignPositionsToBenefit(BenefitPositionDTO request) {
        Benefit benefit = benefitRepository.findById(request.getBenefitId())
                .orElseThrow(() -> new RuntimeException("Phúc lợi không tồn tại"));

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
                .orElseThrow(() -> new RuntimeException("Phúc lợi không tồn tại"));

        Position position = positionRepository.findById(positionId)
                .orElseThrow(() -> new RuntimeException("Vị trí không tồn tại"));

        if (!benefitPositionRepository.existsByBenefitAndPosition(benefit, position)) {
            throw new RuntimeException("Vị trí này chưa được gán cho phúc lợi này.");
        }

        benefitPositionRepository.deleteByBenefitAndPosition(benefit, position);
    }

    public void updateFormula(BenefitPositionUpdateDTO request) {

        // Tìm Benefit từ benefitId
        Benefit benefit = benefitRepository.findById(request.getBenefitId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phúc lợi với ID: " + request.getBenefitId()));

        // Nếu loại phúc lợi là SU_KIEN thì không cho phép cập nhật
        if (BenefitType.SU_KIEN.equals(benefit.getBenefitType())) {
            throw new RuntimeException("Không thể cập nhật công thức cho loại phúc lợi 'SU_KIEN'.");
        }

        BenefitPosition bp = benefitPositionRepository
                .findByBenefit_IdAndPosition_PositionId(request.getBenefitId(), request.getPositionId())
                .orElseThrow(() -> new RuntimeException("Phúc lợi này không có vị trí nào được gán."));

        bp.setFormulaType(request.getFormulaType());
        bp.setFormulaValue(request.getFormulaValue());

        benefitPositionRepository.save(bp);
    }


//    private BigDecimal applySalaryChange(BigDecimal salary, FormulaType type, BigDecimal value, String benefitType, boolean isUndo) {
//        BigDecimal delta = BigDecimal.ZERO;
//
//        if (type == FormulaType.AMOUNT) {
//            delta = value;
//        } else if (type == FormulaType.PERCENTAGE) {
//            delta = salary.multiply(value).divide(BigDecimal.valueOf(100));
//        }
//
//        if ("PHU_CAP".equals(benefitType)) {
//            return isUndo ? salary.subtract(delta) : salary.add(delta);
//        } else if ("KHAU_TRU".equals(benefitType)) {
//            return isUndo ? salary.add(delta) : salary.subtract(delta);
//        }
//
//        return salary;
//    }

}
