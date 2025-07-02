package sep490.com.example.hrms_backend.service;

import sep490.com.example.hrms_backend.dto.DepartmentWorkScheduleViewDTO;
import sep490.com.example.hrms_backend.dto.WorkScheduleDetailCreateDTO;
import sep490.com.example.hrms_backend.dto.WorkScheduleDetailResponseDTO;

import java.util.List;

public interface WorkScheduleDetailService {
    WorkScheduleDetailResponseDTO create(WorkScheduleDetailCreateDTO dto);
    List<DepartmentWorkScheduleViewDTO> getMonthlyWorkSchedule(int month, int year);


}
