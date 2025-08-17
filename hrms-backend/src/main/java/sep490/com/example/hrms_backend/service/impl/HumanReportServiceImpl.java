package sep490.com.example.hrms_backend.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sep490.com.example.hrms_backend.dto.AttendanceMonthlyViewDTO;
import sep490.com.example.hrms_backend.dto.EmployeeResponseDTO;
import sep490.com.example.hrms_backend.entity.AttendanceRecord;
import sep490.com.example.hrms_backend.entity.Department;
import sep490.com.example.hrms_backend.entity.Employee;
import sep490.com.example.hrms_backend.entity.Line;
import sep490.com.example.hrms_backend.mapper.AttendenceReportMapper;
import sep490.com.example.hrms_backend.mapper.EmployeeMapper;
import sep490.com.example.hrms_backend.repository.AttendanceRecordRepository;
import sep490.com.example.hrms_backend.repository.DepartmentRepository;
import sep490.com.example.hrms_backend.repository.EmployeeRepository;
import sep490.com.example.hrms_backend.repository.LineRepository;
import sep490.com.example.hrms_backend.service.HumanReportService;
import sep490.com.example.hrms_backend.utils.HumanReportExcelExport;

import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class HumanReportServiceImpl implements HumanReportService {

    @Autowired
    AttendanceRecordRepository attendanceRecordRepository;
    @Autowired
    EmployeeRepository employeeRepository;
    @Autowired
    DepartmentRepository departmentRepository;
    @Autowired
    LineRepository lineRepository;

    //tested
    @Override
    public Map<String, List<AttendanceMonthlyViewDTO>> getFullEmp(LocalDate date) {

        Map<String, List<AttendanceMonthlyViewDTO>> fullEmp = new HashMap<>();

        List<Department> departmentList = departmentRepository.findAll();

        Department department = departmentRepository.findByDepartmentNameIgnoreCase("Sản Xuất");

        List<Line> lineList = lineRepository.findAll();

        for (Department d : departmentList) {
            if (d != department) {
                List<AttendanceRecord> attendanceRecordList = attendanceRecordRepository.getEmployees(date, d.getDepartmentId());
                List<AttendanceMonthlyViewDTO> attendanceMonthlyViewDTOList = AttendenceReportMapper.mapToAttendanceMonthlyViewDTOList(attendanceRecordList);
                fullEmp.put(d.getDepartmentName(), attendanceMonthlyViewDTOList);
            }
        }

        for (Line l : lineList) {
            List<AttendanceRecord> attendanceRecordList = attendanceRecordRepository.findAllEmpByDateLine(date, l.getLineId());
            List<AttendanceMonthlyViewDTO> attendanceMonthlyViewDTOList = AttendenceReportMapper.mapToAttendanceMonthlyViewDTOList(attendanceRecordList);

            fullEmp.put(l.getLineName(), attendanceMonthlyViewDTOList);
        }

        return fullEmp;
    }

    //tested
    @Override
    public Map<String, List<AttendanceMonthlyViewDTO>> getListEmpAbsent(LocalDate date) {

        Map<String, List<AttendanceMonthlyViewDTO>> absentEmp = new HashMap<>();

        List<Department> departmentList = departmentRepository.findAll();

        Department department = departmentRepository.findByDepartmentNameIgnoreCase("Sản Xuất");

        List<Line> lineList = lineRepository.findAll();

        for (Department d : departmentList) {
            if (d != department) {
                List<AttendanceRecord> attendanceRecordList = attendanceRecordRepository.findAbsentEmpByDateDepartment(date, d.getDepartmentId());
                List<AttendanceMonthlyViewDTO> attendanceMonthlyViewDTOList = AttendenceReportMapper.mapToAttendanceMonthlyViewDTOList(attendanceRecordList);
                absentEmp.put(d.getDepartmentName(), attendanceMonthlyViewDTOList);
            }
        }
        for (Line l : lineList) {
            List<AttendanceRecord> attendanceRecordList = attendanceRecordRepository.findAbsentEmpByDateLine(date, l.getLineId());
            List<AttendanceMonthlyViewDTO> attendanceMonthlyViewDTOList = AttendenceReportMapper.mapToAttendanceMonthlyViewDTOList(attendanceRecordList);
            absentEmp.put(l.getLineName(), attendanceMonthlyViewDTOList);
        }
        return absentEmp;
    }

    //tested
    @Override
    public Map<String, List<AttendanceMonthlyViewDTO>> getListEmpAbsentKL(LocalDate date) {

        Map<String, List<AttendanceMonthlyViewDTO>> absentEmp = new HashMap<>();

        List<Department> departmentList = departmentRepository.findAll();

        Department department = departmentRepository.findByDepartmentNameIgnoreCase("Sản Xuất");

        List<Line> lineList = lineRepository.findAll();

        for (Department d : departmentList) {
            if (d != department) {
                List<AttendanceRecord> attendanceRecordList = attendanceRecordRepository.findAbsentEmpByDateDepartmentKL(date, d.getDepartmentId());
                List<AttendanceMonthlyViewDTO> attendanceMonthlyViewDTOList = AttendenceReportMapper.mapToAttendanceMonthlyViewDTOList(attendanceRecordList);
                absentEmp.put(d.getDepartmentName(), attendanceMonthlyViewDTOList);
            }
        }
        for (Line l : lineList) {
            List<AttendanceRecord> attendanceRecordList = attendanceRecordRepository.findAbsentEmpByDateLineKL(date, l.getLineId());
            List<AttendanceMonthlyViewDTO> attendanceMonthlyViewDTOList = AttendenceReportMapper.mapToAttendanceMonthlyViewDTOList(attendanceRecordList);
            absentEmp.put(l.getLineName(), attendanceMonthlyViewDTOList);
        }
        return absentEmp;
    }

    //tested
    @Override
    public ByteArrayInputStream exportHumanReportToExcel(LocalDate date) {

        List<Department> departments = departmentRepository.findAll();
        Department sanXuat = departmentRepository.findByDepartmentNameIgnoreCase("Sản Xuất");
        List<Line> lines = lineRepository.findAll();

        Map<String, List<AttendanceMonthlyViewDTO>> fullEmp = getFullEmp(date);
        Map<String, List<AttendanceMonthlyViewDTO>> absentEmp = getListEmpAbsent(date);
        Map<String, List<AttendanceMonthlyViewDTO>> absentEmpKL = getListEmpAbsentKL(date);

        return HumanReportExcelExport.exportToExcel(date, departments, sanXuat, lines, fullEmp, absentEmp, absentEmpKL);
    }
}
