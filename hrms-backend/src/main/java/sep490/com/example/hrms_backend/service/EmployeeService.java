package sep490.com.example.hrms_backend.service;

import org.springframework.data.domain.Page;
import sep490.com.example.hrms_backend.dto.*;

import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import java.util.List;

public interface EmployeeService {
    EmployeeResponseDTO createEmployee(EmployeeRequestDTO dto);
    EmployeeResponseDTO updateEmployee(Long id, EmployeeUpdateDTO dto);
    EmployeeDetailDTO getEmployeeDetailById(Long id);

    EmployeeDetailDTO getOwnProfile();
    EmployeeDetailDTO updateOwnProfile(EmployeeOwnProfileUpdateDTO dto);
    void softDeleteEmployee(Long id);
    ByteArrayInputStream exportEmployeesToExcel();
    List<EmployeeResponseDTO> getEmployeeByDepartmentId(Long id);


    List<EmployeeResponseDTO> getEmployeesNotInLine(Long lineId, String search);
    List<EmployeeResponseDTO> getEmployeeByLineId(Long id);

    void addEmployeesToLine(Long lineId, List<Long> employeeIds, Long senderId);
    String getNextEmployeeCodeByPosition(Long positionId);

    String getNextEmployeeCode();
    public List<GenderDistributionDTO> getGenderDistribution(LocalDate startDate, LocalDate endDate);
    public List<DepartmentDistributionDTO> getDepartmentDistribution(LocalDate startDate, LocalDate endDate);

    Page<EmployeeResponseDTO> getAllEmployees(int page, int size, String search, Long departmentId, Long positionId, Long lineId);
}
