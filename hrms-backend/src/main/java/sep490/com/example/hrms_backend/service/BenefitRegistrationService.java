package sep490.com.example.hrms_backend.service;

import org.springframework.stereotype.Service;
import sep490.com.example.hrms_backend.dto.benefit.*;

import java.util.List;

public interface BenefitRegistrationService {
    BenefitResponse searchBenefitByEmployee(Long employeeId, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);

    BenefitRegistrationDTO registerBenefitForEmployee(Long benefitId, Long employeeId, String note);

    void quickRegister(BenefitManualRegistrationRequest request);

    List<EmployeeBasicDetailResponse> searchUnregisteredEmployees(Long benefitId, Long positionId, String keyword);

    void unRegister(Long benefitId, Long positionId, Long employeeId);

    void quickRegisterAll(BenefitMultiPositionRequestDTO request);
}
