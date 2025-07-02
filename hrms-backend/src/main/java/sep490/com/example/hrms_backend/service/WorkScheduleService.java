package sep490.com.example.hrms_backend.service;

import sep490.com.example.hrms_backend.dto.WorkScheduleCreateDTO;
import sep490.com.example.hrms_backend.dto.WorkScheduleResponseDTO;

import java.time.LocalDate;
import java.util.List;

public interface WorkScheduleService {
    List<WorkScheduleResponseDTO> createWorkSchedulesForAll(WorkScheduleCreateDTO dto);
    List<com.example.hrms_backend.dto.WorkScheduleMonthDTO> getAvailableMonths();
    Long resolveWorkScheduleId(Long departmentId, Long lineId, LocalDate dateWork);
    void submitAllWorkSchedules(int month, int year);
    void acceptAllSubmittedSchedules(int month, int year);
}
