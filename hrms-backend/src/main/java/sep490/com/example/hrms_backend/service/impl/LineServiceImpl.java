package sep490.com.example.hrms_backend.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sep490.com.example.hrms_backend.dto.DepartmentDTO;
import sep490.com.example.hrms_backend.entity.Line;
import sep490.com.example.hrms_backend.repository.LineRepository;
import sep490.com.example.hrms_backend.service.LineService;

@Service
@RequiredArgsConstructor
public class LineServiceImpl implements LineService {

    private final LineRepository lineRepository;

    @Override
    public DepartmentDTO getDepartmentByLineId(Long lineId) {
        Line line = lineRepository.findById(lineId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy tổ"));

        return new DepartmentDTO(
                line.getDepartment().getDepartmentId(),
                line.getDepartment().getDepartmentName()
        );
    }
}
