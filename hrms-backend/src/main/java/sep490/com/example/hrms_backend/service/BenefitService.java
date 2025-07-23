package sep490.com.example.hrms_backend.service;

import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import sep490.com.example.hrms_backend.dto.benefit.BenefitDTO;
import sep490.com.example.hrms_backend.dto.benefit.BenefitResponse;
import sep490.com.example.hrms_backend.dto.benefit.PatchBenefitDTO;
import sep490.com.example.hrms_backend.enums.BenefitType;

import java.time.LocalDate;

@Service
public interface BenefitService {
    BenefitResponse getAllBenefitsForHr(String username, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder, String title, String description, Boolean isActive, LocalDate startDate, LocalDate endDate, Integer minParticipants, Integer maxParticipants, BenefitType benefitType);

    BenefitDTO addBenefit(@Valid BenefitDTO benefitDTO);

    BenefitDTO updateBenefit(@Valid PatchBenefitDTO benefitDTO, Long benefitId);

    BenefitDTO updateInactiveStatus(Long id, boolean inactive);

    BenefitResponse searchBenefitByKeyword(String keyword, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);

    BenefitDTO deleteBenefit(Long benefitId);

    BenefitResponse getEmployeeAndPositionRegistrationByBenefitId(Long benefitId, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);

    BenefitDTO getBenefitById(Long id);
}
