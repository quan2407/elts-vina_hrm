package sep490.com.example.hrms_backend.service;

import sep490.com.example.hrms_backend.dto.benefit.BenefitRegistrationDTO;
import sep490.com.example.hrms_backend.dto.benefit.BenefitManualRegistrationRequest;
import sep490.com.example.hrms_backend.dto.benefit.BenefitResponse;
import sep490.com.example.hrms_backend.dto.benefit.EmployeeBasicDetailResponse;

import java.util.List;

public interface BenefitRegistrationService {
    BenefitResponse searchBenefitByEmployee(Long employeeId, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);

    BenefitRegistrationDTO registerBenefitForEmployee(Long benefitId, Long employeeId, String note);

    void quickRegister(BenefitManualRegistrationRequest request);

    List<EmployeeBasicDetailResponse> searchUnregisteredEmployees(Long benefitId, Long positionId, String keyword);
}
