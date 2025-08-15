package sep490.com.example.hrms_backend.service;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
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

    int unRegisterMany(Long benefitId, Long positionId, @NotNull @NotEmpty List<Long> employeeIds);

    PositionRegistrationStatsDTO getRegistrationStats(Long benefitId, Long positionId);
}
