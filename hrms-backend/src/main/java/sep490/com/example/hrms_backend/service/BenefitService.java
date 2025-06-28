package sep490.com.example.hrms_backend.service;

import jakarta.validation.Valid;
import sep490.com.example.hrms_backend.dto.benefit.BenefitDTO;
import sep490.com.example.hrms_backend.dto.benefit.BenefitResponse;

import java.time.LocalDate;


public interface BenefitService {
    BenefitResponse getAllBenefits(String username, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder, String title, String description, Boolean isActive, LocalDate startDate, LocalDate endDate, Integer minParticipants, Integer maxParticipants);

    BenefitDTO addBenefit(@Valid BenefitDTO benefitDTO);

    BenefitDTO updateBenefit(@Valid BenefitDTO benefitDTO, Long benefitId);

    BenefitDTO updateInactiveStatus(Long id, boolean inactive);

    BenefitResponse searchBenefitByKeyword(String keyword, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);

}
