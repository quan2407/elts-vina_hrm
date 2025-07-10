package sep490.com.example.hrms_backend.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sep490.com.example.hrms_backend.dto.DepartmentDTO;
import sep490.com.example.hrms_backend.dto.LinePMCDto;
import sep490.com.example.hrms_backend.entity.Line;
import sep490.com.example.hrms_backend.mapper.LinePMCMapper;
import sep490.com.example.hrms_backend.repository.LineRepository;
import sep490.com.example.hrms_backend.service.LineService;

import java.util.List;

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

    @Override
    public List<LinePMCDto> getAllLine(String search) {
        List<Line> lineServiceList;
        if (search != null && !search.isEmpty()) {
            lineServiceList = lineRepository.findByLineNameContainingIgnoreCase(search);
        } else {
            lineServiceList = lineRepository.findAll();
        }
        return lineServiceList.stream().map(LinePMCMapper::mapToLinePMCDto)
                .toList();
    }
}
