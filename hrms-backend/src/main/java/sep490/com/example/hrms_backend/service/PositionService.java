package sep490.com.example.hrms_backend.service;

import org.springframework.stereotype.Service;
import sep490.com.example.hrms_backend.dto.PositionDTO;

import java.util.List;

@Service
public interface PositionService {

    List<PositionDTO> getPositionsNotRegisteredToBenefit(Long benefitId);
}
