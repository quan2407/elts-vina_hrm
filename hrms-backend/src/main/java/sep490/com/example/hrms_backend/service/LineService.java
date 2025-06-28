package sep490.com.example.hrms_backend.service;

import sep490.com.example.hrms_backend.dto.DepartmentDTO;

public interface LineService {
    DepartmentDTO getDepartmentByLineId(Long lineId);
}
