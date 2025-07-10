package sep490.com.example.hrms_backend.service;

import sep490.com.example.hrms_backend.dto.DepartmentDTO;
import sep490.com.example.hrms_backend.dto.LineDTO;
import sep490.com.example.hrms_backend.dto.LinePMCDto;

import java.util.List;

public interface LineService {
    DepartmentDTO getDepartmentByLineId(Long lineId);

    List<LinePMCDto> getAllLine(String search);

    LineDTO getLineByLineId(Long lineId);

    void assignLeaderToLine(Long lineId, Long leaderId);
}
