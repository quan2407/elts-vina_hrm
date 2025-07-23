package sep490.com.example.hrms_backend.service.impl;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sep490.com.example.hrms_backend.dto.PositionDTO;
import sep490.com.example.hrms_backend.dto.benefit.BenefitDTO;
import sep490.com.example.hrms_backend.entity.BenefitPosition;
import sep490.com.example.hrms_backend.entity.Position;
import sep490.com.example.hrms_backend.repository.BenefitPositionRepository;
import sep490.com.example.hrms_backend.repository.BenefitRepository;
import sep490.com.example.hrms_backend.repository.PositionRepository;
import sep490.com.example.hrms_backend.service.PositionService;

import java.util.List;

@Service
public class PositionServiceImpl implements PositionService {

    @Autowired
    private PositionRepository positionRepository;

    @Autowired
    private BenefitPositionRepository benefitPositionRepository;

    @Autowired
    private ModelMapper modelMapper;


    @Override
    public List<PositionDTO> getPositionsNotRegisteredToBenefit(Long benefitId) {
        // 1. Lấy danh sách ID position đã đăng ký benefit này
        List<Long> registeredPositionIds = benefitPositionRepository.findPositionIdsByBenefitId(benefitId);

        // 2. Lấy tất cả position KHÔNG thuộc danh sách trên
        List<Position> availablePositions ;
        // 2. Nếu chưa gán cho position nào thì trả về toàn bộ danh sách
        if (registeredPositionIds == null || registeredPositionIds.isEmpty()) {
            availablePositions = positionRepository.findAll();
        } else {
            availablePositions = positionRepository.findByPositionIdNotIn(registeredPositionIds);
        }
        // 3. Convert sang DTO
        List<PositionDTO> positionDTOList = availablePositions.stream()
                .map(position -> modelMapper.map(position, PositionDTO.class)).toList();

        return positionDTOList;

    }
}
