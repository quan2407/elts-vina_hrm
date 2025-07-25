package sep490.com.example.hrms_backend.service;

import sep490.com.example.hrms_backend.dto.AttendanceMonthlyViewDTO;
import sep490.com.example.hrms_backend.dto.EmployeeResponseDTO;

import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface HumanReportService {

    Map<String, List<AttendanceMonthlyViewDTO>> getFullEmp(LocalDate date);

    Map<String, List<AttendanceMonthlyViewDTO>> getListEmpAbsent(LocalDate date);

    Map<String, List<AttendanceMonthlyViewDTO>> getListEmpAbsentKL(LocalDate date);

    ByteArrayInputStream exportHumanReportToExcel(LocalDate date);
}
