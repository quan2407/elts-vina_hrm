package sep490.com.example.hrms_backend.service;

import org.springframework.stereotype.Service;
import sep490.com.example.hrms_backend.dto.benefit.BenefitRegistrationDTO;
import sep490.com.example.hrms_backend.dto.benefit.BenefitRegistrationResponse;
import sep490.com.example.hrms_backend.dto.benefit.BenefitResponse;

public interface BenefitRegistrationService {
    BenefitResponse searchBenefitByEmployee(Long employeeId, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);

    BenefitRegistrationDTO registerBenefitForEmployee(Long benefitId, Long employeeId, String note);
}
