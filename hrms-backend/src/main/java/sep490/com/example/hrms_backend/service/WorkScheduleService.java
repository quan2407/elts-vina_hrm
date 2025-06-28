package sep490.com.example.hrms_backend.service;

import sep490.com.example.hrms_backend.dto.WorkScheduleCreateDTO;
import sep490.com.example.hrms_backend.dto.WorkScheduleResponseDTO;

import java.util.List;

public interface WorkScheduleService {
    List<WorkScheduleResponseDTO> createWorkSchedulesForAll(WorkScheduleCreateDTO dto);

}
