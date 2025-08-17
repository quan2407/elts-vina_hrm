package sep490.com.example.hrms_backend.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import sep490.com.example.hrms_backend.dto.*;
import sep490.com.example.hrms_backend.entity.*;
import sep490.com.example.hrms_backend.repository.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AttendanceRecordServiceImplTest {

    // 1. Mock tất cả các dependency
    @Mock
    private WorkScheduleRepository workScheduleRepository;
    @Mock
    private AttendanceRecordRepository attendanceRecordRepository;
    @Mock
    private EmployeeRepository employeeRepository;
    @Mock
    private HolidayRepository holidayRepository;
    @Mock
    private WorkScheduleServiceImpl workScheduleService;
    @Mock
    private WorkScheduleDetailRepository workScheduleDetailRepository;

    // 2. Tiêm mock vào service cần test
    @InjectMocks
    private AttendanceRecordServiceImpl attendanceRecordService;

    // 3. Khai báo dữ liệu mẫu
    private Employee employee;
    private WorkSchedule workSchedule;
    private List<AttendanceRecord> records;

    // 4. Thiết lập dữ liệu mẫu trước mỗi test
    @BeforeEach
    void setUp() {
        // --- Nhân viên ---
        Position position = new Position();
        position.setPositionName("Developer");
        Department department = new Department();
        department.setDepartmentName("IT");

        employee = new Employee();
        employee.setEmployeeId(1L);
        employee.setEmployeeCode("EMP001");
        employee.setEmployeeName("Nguyễn Văn A");
        employee.setPosition(position);
        employee.setDepartment(department);

        // --- Lịch làm việc ---
        workSchedule = new WorkSchedule();
        workSchedule.setId(10L);

        // Chi tiết lịch làm việc cho ngày thường và cuối tuần
        WorkScheduleDetail workDayDetail = new WorkScheduleDetail();
        workDayDetail.setDateWork(LocalDate.of(2024, 8, 1)); // Thứ Năm

        WorkScheduleDetail weekendDetail = new WorkScheduleDetail();
        weekendDetail.setDateWork(LocalDate.of(2024, 8, 4)); // Chủ Nhật

        workSchedule.setWorkScheduleDetails(List.of(workDayDetail, weekendDetail));

        // --- Dữ liệu chấm công ---
        // Ngày thường
        AttendanceRecord record1 = new AttendanceRecord();
        record1.setId(101L);
        record1.setEmployee(employee);
        record1.setDate(LocalDate.of(2024, 8, 1));
        record1.setDayShift("8");
        record1.setOtShift("2");
        record1.setWorkSchedule(workSchedule);
        record1.setCheckInTime(LocalTime.of(8, 0));
        record1.setCheckOutTime(LocalTime.of(19, 0)); // 8h làm + 1h nghỉ + 2h OT

        // Ngày cuối tuần (Chủ Nhật)
        AttendanceRecord record2 = new AttendanceRecord();
        record2.setId(102L);
        record2.setEmployee(employee);
        record2.setDate(LocalDate.of(2024, 8, 4));
        record2.setWeekendShift("4");
        record2.setWorkSchedule(workSchedule);

        // Ngày lễ
        AttendanceRecord record3 = new AttendanceRecord();
        record3.setId(103L);
        record3.setEmployee(employee);
        record3.setDate(LocalDate.of(2024, 8, 30));
        record3.setHolidayShift("8");

        records = List.of(record1, record2, record3);
    }

    //================================================================================
    // Test cho phương thức getEmpMonthlyAttendanceById
    //================================================================================

    @Test
    @DisplayName("Test getEmpMonthlyAttendanceById - Trả về dữ liệu chấm công tháng thành công")
    void getEmpMonthlyAttendanceById_Success() {
        // Arrange
        Long employeeId = 1L;
        int month = 8;
        int year = 2024;

        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(employee));
        when(attendanceRecordRepository.findByEmpIdAndMonthAndYear(employeeId, month, year)).thenReturn(records);

        // SỬA LỖI: Xóa dòng mock thừa thãi. Dòng dưới đây đã bao gồm tất cả các trường hợp.
        when(holidayRepository.existsByStartDateLessThanEqualAndEndDateGreaterThanEqual(any(LocalDate.class)))
                .thenAnswer(invocation -> {
                    LocalDate date = invocation.getArgument(0);
                    return date.equals(LocalDate.of(2024, 8, 30));
                });

        // Act
        List<AttendanceMonthlyViewDTO> resultList = attendanceRecordService.getEmpMonthlyAttendanceById(employeeId, month, year);

        // Assert
        assertNotNull(resultList);
        assertEquals(1, resultList.size());

        AttendanceMonthlyViewDTO result = resultList.get(0);
        assertEquals("EMP001", result.getEmployeeCode());
        assertEquals("Nguyễn Văn A", result.getEmployeeName());
        assertEquals(3, result.getAttendanceByDate().size()); // Phải có 3 ngày chấm công

        // Kiểm tra chi tiết từng ngày
        // Ngày thường
        AttendanceCellDTO day1 = result.getAttendanceByDate().get("1");
        assertEquals("8", day1.getShift());
        assertEquals("2", day1.getOvertime());
        assertTrue(day1.isHasScheduleDetail());
        assertFalse(day1.isWeekendFlag());
        assertFalse(day1.isHolidayFlag());

        // Ngày cuối tuần
        AttendanceCellDTO day4 = result.getAttendanceByDate().get("4");
        assertEquals("4", day4.getWeekend());
        assertTrue(day4.isHasScheduleDetail());
        assertTrue(day4.isWeekendFlag());
        assertFalse(day4.isHolidayFlag());

        // Ngày lễ
        AttendanceCellDTO day30 = result.getAttendanceByDate().get("30");
        assertEquals("8", day30.getHoliday());
        assertFalse(day30.isHasScheduleDetail()); // Không có lịch làm việc cho ngày này
        assertFalse(day30.isWeekendFlag());
        assertTrue(day30.isHolidayFlag());

        // Kiểm tra tổng số giờ
        assertEquals(8.0f, result.getTotalDayShiftHours());
        assertEquals(2.0f, result.getTotalOvertimeHours());
        assertEquals(4.0f, result.getTotalWeekendHours());
        assertEquals(8.0f, result.getTotalHolidayHours());
        assertEquals(22.0f, result.getTotalHours());
    }

    @Test
    @DisplayName("Test getEmpMonthlyAttendanceById - Ném NullPointerException khi không tìm thấy nhân viên")
    void getEmpMonthlyAttendanceById_ThrowsNPE_WhenEmployeeNotFound() {
        // Arrange
        Long nonExistentEmployeeId = 99L;
        when(employeeRepository.findById(nonExistentEmployeeId)).thenReturn(Optional.empty());

        // Act & Assert
        // Code gốc sẽ ném NPE vì gọi .getEmployeeCode() trên đối tượng null
        assertThrows(NullPointerException.class, () -> {
            attendanceRecordService.getEmpMonthlyAttendanceById(nonExistentEmployeeId, 8, 2024);
        });
    }

    @Test
    @DisplayName("Test getEmpMonthlyAttendanceById - Trả về kết quả rỗng khi không có bản ghi chấm công")
    void getEmpMonthlyAttendanceById_ReturnsEmptyData_WhenNoRecordsFound() {
        // Arrange
        Long employeeId = 1L;
        int month = 9;
        int year = 2024;

        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(employee));
        // Giả lập không có bản ghi nào trong tháng 9
        when(attendanceRecordRepository.findByEmpIdAndMonthAndYear(employeeId, month, year)).thenReturn(Collections.emptyList());

        // Act
        List<AttendanceMonthlyViewDTO> resultList = attendanceRecordService.getEmpMonthlyAttendanceById(employeeId, month, year);

        // Assert
        assertNotNull(resultList);
        assertEquals(1, resultList.size());

        AttendanceMonthlyViewDTO result = resultList.get(0);
        assertEquals("EMP001", result.getEmployeeCode());
        assertTrue(result.getAttendanceByDate().isEmpty()); // Bảng chấm công rỗng
        assertEquals(0f, result.getTotalHours()); // Tổng giờ là 0
    }


    //================================================================================
    // Test cho phương thức getMonthlyAttendance
    //================================================================================

    @Test
    @DisplayName("Test getMonthlyAttendance - Lấy dữ liệu thành công không có từ khóa tìm kiếm")
    void getMonthlyAttendance_Success_WithoutSearchKeyword() {
        // Arrange
        int month = 8;
        int year = 2024;
        Pageable pageable = PageRequest.of(0, 10);

        Employee employee2 = new Employee();
        employee2.setEmployeeId(2L);
        employee2.setEmployeeCode("EMP002");
        employee2.setEmployeeName("Trần Thị C");

        Page<Employee> employeePage = new PageImpl<>(List.of(employee, employee2), pageable, 2);

        when(employeeRepository.findAllActive(pageable)).thenReturn(employeePage);
        when(attendanceRecordRepository.findByMonthAndYear(month, year)).thenReturn(records); // Dùng records từ setUp
        when(holidayRepository.existsByStartDateLessThanEqualAndEndDateGreaterThanEqual(any(LocalDate.class))).thenReturn(false);

        // Act
        Page<AttendanceMonthlyViewDTO> result = attendanceRecordService.getMonthlyAttendance(month, year, 0, 10, null,1l,2l,3l);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertEquals(2, result.getContent().size());

        // Kiểm tra nhân viên đầu tiên (có dữ liệu chấm công)
        AttendanceMonthlyViewDTO dto1 = result.getContent().stream().filter(d -> d.getEmployeeId().equals(1L)).findFirst().orElse(null);
        assertNotNull(dto1);
        assertEquals(22.0f, dto1.getTotalHours());

        // Kiểm tra nhân viên thứ hai (không có dữ liệu chấm công)
        AttendanceMonthlyViewDTO dto2 = result.getContent().stream().filter(d -> d.getEmployeeId().equals(2L)).findFirst().orElse(null);
        assertNotNull(dto2);
        assertEquals(0f, dto2.getTotalHours());

        // Verify
        verify(employeeRepository, times(1)).findAllActive(pageable);
    }

    @Test
    @DisplayName("Test getMonthlyAttendance - Lấy dữ liệu thành công với từ khóa tìm kiếm")
    void getMonthlyAttendance_Success_WithSearchKeyword() {
        // Arrange
        int month = 8;
        int year = 2024;
        String search = "nguyễn";

        // Khi có search, service sẽ lấy tất cả nhân viên active để lọc
        when(employeeRepository.findAllActive()).thenReturn(List.of(employee));
        when(attendanceRecordRepository.findByMonthAndYear(month, year)).thenReturn(records);
        when(holidayRepository.existsByStartDateLessThanEqualAndEndDateGreaterThanEqual(any(LocalDate.class))).thenReturn(false);

        // Act
        Page<AttendanceMonthlyViewDTO> result = attendanceRecordService.getMonthlyAttendance(month, year, 0, 10, search,1l,2l,3l);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements()); // Chỉ tìm thấy 1 nhân viên khớp
        assertEquals(1, result.getContent().size());
        assertEquals("Nguyễn Văn A", result.getContent().get(0).getEmployeeName());
        assertEquals(22.0f, result.getContent().get(0).getTotalHours());

        // Verify
        verify(employeeRepository, times(1)).findAllActive(); // Gọi hàm không phân trang
    }

    //================================================================================
    // Test cho phương thức parseHour (gián tiếp)
    //================================================================================

    @Test
    @DisplayName("Test parseHour (gián tiếp) - Tính toán giờ chính xác từ các mã nghỉ phép")
    void getEmpMonthlyAttendanceById_CalculatesHoursFromLeaveCodesCorrectly() {
        // Arrange
        Long employeeId = 1L;
        int month = 8;
        int year = 2024;

        // Tạo các bản ghi chấm công với các mã nghỉ phép
        AttendanceRecord recordP = new AttendanceRecord();
        recordP.setEmployee(employee);
        recordP.setDate(LocalDate.of(2024, 8, 5));
        recordP.setDayShift("P"); // Nghỉ phép (8 giờ)

        AttendanceRecord recordP2 = new AttendanceRecord();
        recordP2.setEmployee(employee);
        recordP2.setDate(LocalDate.of(2024, 8, 6));
        recordP2.setDayShift("P_2"); // Nửa ngày phép (4 giờ)

        AttendanceRecord recordKL = new AttendanceRecord();
        recordKL.setEmployee(employee);
        recordKL.setDate(LocalDate.of(2024, 8, 7));
        recordKL.setDayShift("KL"); // Không lý do (0 giờ)

        AttendanceRecord recordInvalid = new AttendanceRecord();
        recordInvalid.setEmployee(employee);
        recordInvalid.setDate(LocalDate.of(2024, 8, 8));
        recordInvalid.setDayShift("INVALID_CODE"); // Mã không hợp lệ (0 giờ)

        List<AttendanceRecord> leaveCodeRecords = List.of(recordP, recordP2, recordKL, recordInvalid);

        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(employee));
        when(attendanceRecordRepository.findByEmpIdAndMonthAndYear(employeeId, month, year)).thenReturn(leaveCodeRecords);
        when(holidayRepository.existsByStartDateLessThanEqualAndEndDateGreaterThanEqual(any(LocalDate.class))).thenReturn(false);

        // Act
        List<AttendanceMonthlyViewDTO> resultList = attendanceRecordService.getEmpMonthlyAttendanceById(employeeId, month, year);

        // Assert
        assertNotNull(resultList);
        assertEquals(1, resultList.size());

        AttendanceMonthlyViewDTO result = resultList.get(0);

        // Tổng số giờ làm việc trong ngày phải là tổng của các mã nghỉ phép
        // 8f (P) + 4f (P_2) + 0f (KL) + 0f (INVALID_CODE) = 12f
        assertEquals(12.0f, result.getTotalDayShiftHours());
        assertEquals(0f, result.getTotalOvertimeHours());
        assertEquals(12.0f, result.getTotalHours());
    }

    //================================================================================
    // Test cho phương thức getAvailableMonths
    //================================================================================

    @Test
    @DisplayName("Test getAvailableMonths - Trả về danh sách tháng/năm thành công")
    void getAvailableMonths_Success() {
        // Arrange
        // Giả lập kết quả trả về từ repository
        List<Object[]> rawList = new ArrayList<>();
        rawList.add(new Object[]{8, 2024}); // [month, year]
        rawList.add(new Object[]{7, 2024}); // [month, year]

        when(attendanceRecordRepository.findDistinctMonthYear()).thenReturn(rawList);

        // Act
        List<MonthYearDTO> result = attendanceRecordService.getAvailableMonths();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(2024, result.get(0).getYear());
        assertEquals(8, result.get(0).getMonth());
        assertEquals(2024, result.get(1).getYear());
        assertEquals(7, result.get(1).getMonth());

        // Verify
        verify(attendanceRecordRepository, times(1)).findDistinctMonthYear();
    }

    //================================================================================
    // Test cho phương thức updateCheckInOut
    //================================================================================

    @Test
    @DisplayName("Test updateCheckInOut - Cập nhật giờ vào/ra thành công")
    void updateCheckInOut_Success() {
        // Arrange
        Long recordId = 101L;
        AttendanceCheckInOutDTO dto = new AttendanceCheckInOutDTO();
        dto.setCheckIn("08:05");
        dto.setCheckOut("17:35");

        // Lấy bản ghi từ setUp (giả sử nó có WorkSchedule)
        AttendanceRecord record = records.get(0);

        when(attendanceRecordRepository.findById(recordId)).thenReturn(Optional.of(record));

        // Act
        // Giả sử phương thức calculateShift sẽ được gọi, chúng ta không test logic bên trong nó
        // mà chỉ kiểm tra kết quả sau khi save
        attendanceRecordService.updateCheckInOut(recordId, dto);

        // Assert & Verify
        ArgumentCaptor<AttendanceRecord> captor = ArgumentCaptor.forClass(AttendanceRecord.class);
        verify(attendanceRecordRepository, times(1)).save(captor.capture());

        AttendanceRecord savedRecord = captor.getValue();
        assertEquals(LocalTime.of(8, 5), savedRecord.getCheckInTime());
        assertEquals(LocalTime.of(17, 35), savedRecord.getCheckOutTime());
    }

    @Test
    @DisplayName("Test updateCheckInOut - Xóa giờ vào/ra khi DTO rỗng")
    void updateCheckInOut_ClearsTime_WhenDtoIsEmpty() {
        // Arrange
        Long recordId = 101L;
        AttendanceCheckInOutDTO dto = new AttendanceCheckInOutDTO();
        dto.setCheckIn(""); // Chuỗi rỗng
        dto.setCheckOut(null); // Null

        AttendanceRecord record = records.get(0);

        when(attendanceRecordRepository.findById(recordId)).thenReturn(Optional.of(record));

        // Act
        attendanceRecordService.updateCheckInOut(recordId, dto);

        // Assert & Verify
        ArgumentCaptor<AttendanceRecord> captor = ArgumentCaptor.forClass(AttendanceRecord.class);
        verify(attendanceRecordRepository, times(1)).save(captor.capture());

        AttendanceRecord savedRecord = captor.getValue();
        assertNull(savedRecord.getCheckInTime());
        assertNull(savedRecord.getCheckOutTime());
        // Kiểm tra công đã bị xóa
        assertNull(savedRecord.getDayShift());
    }

    @Test
    @DisplayName("Test updateCheckInOut - Ném ngoại lệ khi không tìm thấy bản ghi")
    void updateCheckInOut_ThrowsException_WhenRecordNotFound() {
        // Arrange
        Long nonExistentId = 999L;
        when(attendanceRecordRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            attendanceRecordService.updateCheckInOut(nonExistentId, new AttendanceCheckInOutDTO());
        });
    }

    //================================================================================
    // Test cho phương thức updateLeaveCode
    //================================================================================

    @Test
    @DisplayName("Test updateLeaveCode - Cập nhật thành công cho dayShift")
    void updateLeaveCode_Success_ForDayShift() {
        // Arrange
        Long recordId = 101L;
        LeaveCodeUpdateDTO dto = new LeaveCodeUpdateDTO("P", "dayShift");
        AttendanceRecord record = records.get(0);

        when(attendanceRecordRepository.findById(recordId)).thenReturn(Optional.of(record));

        // Act
        attendanceRecordService.updateLeaveCode(recordId, dto);

        // Assert & Verify
        ArgumentCaptor<AttendanceRecord> captor = ArgumentCaptor.forClass(AttendanceRecord.class);
        verify(attendanceRecordRepository, times(1)).save(captor.capture());
        assertEquals("P", captor.getValue().getDayShift());
    }

    @Test
    @DisplayName("Test updateLeaveCode - Ném ngoại lệ khi mã nghỉ phép không hợp lệ")
    void updateLeaveCode_ThrowsException_ForInvalidLeaveCode() {
        // Arrange
        Long recordId = 101L;
        LeaveCodeUpdateDTO dto = new LeaveCodeUpdateDTO("INVALID", "dayShift");

        when(attendanceRecordRepository.findById(recordId)).thenReturn(Optional.of(records.get(0)));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            attendanceRecordService.updateLeaveCode(recordId, dto);
        });
        assertEquals("Invalid leave code: INVALID", exception.getMessage());
    }

    @Test
    @DisplayName("Test updateLeaveCode - Ném ngoại lệ khi trường cập nhật không hợp lệ")
    void updateLeaveCode_ThrowsException_ForInvalidField() {
        // Arrange
        Long recordId = 101L;
        LeaveCodeUpdateDTO dto = new LeaveCodeUpdateDTO("P", "invalidField");

        when(attendanceRecordRepository.findById(recordId)).thenReturn(Optional.of(records.get(0)));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            attendanceRecordService.updateLeaveCode(recordId, dto);
        });
        assertEquals("Invalid field: invalidField", exception.getMessage());
    }

    //================================================================================
    // Test cho phương thức updateDailyAttendanceForDate
    //================================================================================

    @Test
    @DisplayName("Test updateDailyAttendanceForDate - Gọi generate khi có lịch làm việc cho ngày đó")
    void updateDailyAttendanceForDate_CallsGenerate_WhenScheduleExists() {
        // Arrange
        LocalDate dateToUpdate = LocalDate.of(2024, 8, 1);

        // Lấy workSchedule từ setUp, nó đã có detail cho ngày 1/8
        List<WorkSchedule> schedules = List.of(workSchedule);

        when(workScheduleRepository.findByMonthAndYearAndIsAcceptedTrue(8, 2024)).thenReturn(schedules);
        when(workScheduleDetailRepository.findByWorkSchedule_Id(workSchedule.getId())).thenReturn(new ArrayList<>(workSchedule.getWorkScheduleDetails()));

        // Act
        attendanceRecordService.updateDailyAttendanceForDate(dateToUpdate);

        // Assert & Verify
        // Kiểm tra xem phương thức generate có được gọi với đúng lịch làm việc không
        verify(workScheduleService, times(1)).generateAttendanceRecords(workSchedule);
    }

    @Test
    @DisplayName("Test updateDailyAttendanceForDate - Không gọi generate khi không có lịch cho ngày đó")
    void updateDailyAttendanceForDate_DoesNotCallGenerate_WhenScheduleNotExistsForDate() {
        // Arrange
        LocalDate dateToUpdate = LocalDate.of(2024, 8, 15); // Một ngày không có trong lịch
        List<WorkSchedule> schedules = List.of(workSchedule);

        when(workScheduleRepository.findByMonthAndYearAndIsAcceptedTrue(8, 2024)).thenReturn(schedules);
        when(workScheduleDetailRepository.findByWorkSchedule_Id(workSchedule.getId())).thenReturn(new ArrayList<>(workSchedule.getWorkScheduleDetails()));

        // Act
        attendanceRecordService.updateDailyAttendanceForDate(dateToUpdate);

        // Assert & Verify
        // Đảm bảo phương thức generate không bao giờ được gọi
        verify(workScheduleService, never()).generateAttendanceRecords(any());
    }


    //================================================================================
    // Test cho phương thức getAttendanceForExport
    //================================================================================

    @Test
    @DisplayName("Test getAttendanceForExport - Trả về dữ liệu cho tất cả nhân viên thành công")
    void getAttendanceForExport_Success() {
        // Arrange
        int month = 8;
        int year = 2024;

        // Tạo thêm một nhân viên không có bản ghi chấm công
        Employee employee2 = new Employee();
        employee2.setEmployeeId(2L);
        employee2.setEmployeeCode("EMP002");
        employee2.setEmployeeName("Trần Thị C");

        List<Employee> allEmployees = List.of(employee, employee2);

        // Giả lập các lời gọi repository
        when(employeeRepository.findAllActive()).thenReturn(allEmployees);
        // Dùng lại biến 'records' từ setUp, chỉ chứa bản ghi của employee 1
        when(attendanceRecordRepository.findByMonthAndYear(month, year)).thenReturn(records);
        when(holidayRepository.existsByStartDateLessThanEqualAndEndDateGreaterThanEqual(any(LocalDate.class))).thenReturn(false);


        // Act
        List<AttendanceMonthlyViewDTO> resultList = attendanceRecordService.getAttendanceForExport(month, year);

        // Assert
        assertNotNull(resultList);
        assertEquals(2, resultList.size(), "Phải trả về kết quả cho cả 2 nhân viên");

        // Kiểm tra nhân viên 1 (Nguyễn Văn A) - người có dữ liệu chấm công
        AttendanceMonthlyViewDTO dto1 = resultList.stream()
                .filter(dto -> dto.getEmployeeCode().equals("EMP001"))
                .findFirst().orElse(null);
        assertNotNull(dto1);
        assertEquals(22.0f, dto1.getTotalHours(), "Tổng giờ của nhân viên A phải được tính toán đúng");
        assertFalse(dto1.getAttendanceByDate().isEmpty(), "Phải có chi tiết chấm công cho nhân viên A");

        // Kiểm tra nhân viên 2 (Trần Thị C) - người không có dữ liệu chấm công
        AttendanceMonthlyViewDTO dto2 = resultList.stream()
                .filter(dto -> dto.getEmployeeCode().equals("EMP002"))
                .findFirst().orElse(null);
        assertNotNull(dto2);
        assertEquals(0f, dto2.getTotalHours(), "Tổng giờ của nhân viên C phải là 0");
        assertTrue(dto2.getAttendanceByDate().isEmpty(), "Không được có chi tiết chấm công cho nhân viên C");

        // Verify
        verify(employeeRepository, times(1)).findAllActive();
        verify(attendanceRecordRepository, times(1)).findByMonthAndYear(month, year);
    }

    @Test
    @DisplayName("Test getAttendanceForExport - Trả về danh sách rỗng khi không có nhân viên nào")
    void getAttendanceForExport_ReturnsEmptyList_WhenNoEmployees() {
        // Arrange
        int month = 8;
        int year = 2024;

        // Giả lập không có nhân viên nào
        when(employeeRepository.findAllActive()).thenReturn(Collections.emptyList());

        // Act
        List<AttendanceMonthlyViewDTO> resultList = attendanceRecordService.getAttendanceForExport(month, year);

        // Assert
        assertNotNull(resultList);
        assertTrue(resultList.isEmpty(), "Danh sách kết quả phải rỗng");


    }
}
