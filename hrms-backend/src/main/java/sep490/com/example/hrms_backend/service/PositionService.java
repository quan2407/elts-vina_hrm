package sep490.com.example.hrms_backend.service;

import org.springframework.stereotype.Service;
import sep490.com.example.hrms_backend.dto.PositionDTO;
import sep490.com.example.hrms_backend.dto.benefit.BenefitResponse;

import java.util.List;

@Service
public interface PositionService {

    List<PositionDTO> getPositionsNotRegisteredToBenefit(Long benefitId);


    BenefitResponse getEmployeeByPositionAndBenefit(Long benefitId, Long positionId, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);
}
