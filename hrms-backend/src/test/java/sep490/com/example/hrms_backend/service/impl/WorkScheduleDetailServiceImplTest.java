package sep490.com.example.hrms_backend.service.impl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sep490.com.example.hrms_backend.dto.DepartmentWorkScheduleViewDTO;
import sep490.com.example.hrms_backend.dto.WorkScheduleDetailCreateDTO;
import sep490.com.example.hrms_backend.dto.WorkScheduleDetailResponseDTO;
import sep490.com.example.hrms_backend.entity.Department;
import sep490.com.example.hrms_backend.entity.Line;
import sep490.com.example.hrms_backend.entity.WorkSchedule;
import sep490.com.example.hrms_backend.entity.WorkScheduleDetail;
import sep490.com.example.hrms_backend.repository.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class WorkScheduleDetailServiceImplTest {
    // 1. Mock các dependency
    @Mock
    private WorkScheduleDetailRepository workScheduleDetailRepository;
    @Mock
    private WorkScheduleRepository workScheduleRepository;
    @Mock
    private LineRepository lineRepository;
    @Mock
    private DepartmentRepository departmentRepository;
    @Mock
    private HolidayRepository holidayRepository;

    // 2. Tiêm mock vào service cần test
    @InjectMocks
    private WorkScheduleDetailServiceImpl workScheduleDetailService;

    @InjectMocks
    private WorkScheduleServiceImpl workScheduleService;



    // 3. Khai báo dữ liệu mẫu
    private WorkSchedule workSchedule;
    private WorkScheduleDetailCreateDTO createDTO;
    private WorkScheduleDetail savedDetail;
    private Department department;
    private Department deptSanXuat, deptIT;
    private Line lineA;
    private WorkSchedule scheduleLineA, scheduleIT;
    private WorkScheduleDetail detailLineA, detailIT;
    private int month, year;

    // 4. Thiết lập dữ liệu mẫu trước mỗi test
    @BeforeEach
    void setUp() {
        month = 8;
        year = 2024;

        deptSanXuat = new Department();
        deptSanXuat.setDepartmentId(1L);
        deptSanXuat.setDepartmentName("Sản Xuất");

        deptIT = new Department();
        deptIT.setDepartmentId(2L);
        deptIT.setDepartmentName("IT");

        lineA = new Line();
        lineA.setLineId(100L);
        lineA.setLineName("Chuyền A");
        lineA.setDepartment(deptSanXuat);

        scheduleLineA = new WorkSchedule();
        scheduleLineA.setId(10L);
        scheduleLineA.setLine(lineA);
        scheduleLineA.setDepartment(deptSanXuat);
        scheduleLineA.setMonth(month);
        scheduleLineA.setYear(year);
        scheduleLineA.setSubmitted(true);
        scheduleLineA.setAccepted(true);

        scheduleIT = new WorkSchedule();
        scheduleIT.setId(20L);
        scheduleIT.setLine(null); // Phòng ban không có chuyền
        scheduleIT.setDepartment(deptIT);
        scheduleIT.setMonth(month);
        scheduleIT.setYear(year);

        detailLineA = new WorkScheduleDetail();
        detailLineA.setId(1000L);
        detailLineA.setDateWork(LocalDate.of(year, month, 5)); // Ngày 5
        detailLineA.setStartTime(LocalTime.of(8,0));
        detailLineA.setEndTime(LocalTime.of(17,0));
        detailLineA.setWorkSchedule(scheduleLineA);

        detailIT = new WorkScheduleDetail();
        detailIT.setId(2000L);
        detailIT.setDateWork(LocalDate.of(year, month, 6)); // Ngày 6
        detailIT.setStartTime(LocalTime.of(9,0));
        detailIT.setEndTime(LocalTime.of(18,0));
        detailIT.setWorkSchedule(scheduleIT);

        department = new Department();
        department.setDepartmentId(1L);
        department.setDepartmentName("Sản Xuất");

        workSchedule = new WorkSchedule();
        workSchedule.setId(100L);
        workSchedule.setMonth(8);
        workSchedule.setYear(2024);
        workSchedule.setDepartment(department);

        createDTO = new WorkScheduleDetailCreateDTO();
        createDTO.setWorkScheduleId(100L);
        createDTO.setDateWork(LocalDate.of(2024, 8, 12)); // Thứ Hai
        createDTO.setStartTime(LocalTime.of(8, 0));
        createDTO.setEndTime(LocalTime.of(17, 0));

        savedDetail = new WorkScheduleDetail();
        savedDetail.setId(1L);
        savedDetail.setDateWork(createDTO.getDateWork());
        savedDetail.setStartTime(createDTO.getStartTime());
        savedDetail.setEndTime(createDTO.getEndTime());
        savedDetail.setWorkSchedule(workSchedule);
    }

    //================================================================================
    // Test cho phương thức create
    //================================================================================

    @Test
    @DisplayName("Test create - Tạo thành công cho ngày làm việc bình thường")
    void create_Success_ForNormalWorkday() {
        // Arrange
        when(workScheduleRepository.findById(100L)).thenReturn(Optional.of(workSchedule));
        when(holidayRepository.existsByStartDateLessThanEqualAndEndDateGreaterThanEqual(any(LocalDate.class))).thenReturn(false);

        when(workScheduleDetailRepository.save(any(WorkScheduleDetail.class))).thenAnswer(invocation -> {
            WorkScheduleDetail detailToSave = invocation.getArgument(0);
            detailToSave.setId(1L); // Gán ID giả
            return detailToSave;
        });

        // Act
        WorkScheduleDetailResponseDTO result = workScheduleDetailService.create(createDTO);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(100L, result.getWorkScheduleId());
        assertFalse(result.getIsOvertime(), "Ngày thường không được tính là OT");

        // Verify
        ArgumentCaptor<WorkScheduleDetail> captor = ArgumentCaptor.forClass(WorkScheduleDetail.class);
        verify(workScheduleDetailRepository, times(1)).save(captor.capture());
    }

        @Test
    @DisplayName("Test create - isOvertime phải là true khi kết thúc muộn")
    void create_ShouldSetOvertimeTrue_WhenEndTimeIsLate() {
        // Arrange
        createDTO.setEndTime(LocalTime.of(17, 1)); // Kết thúc sau 17h
        when(workScheduleRepository.findById(100L)).thenReturn(Optional.of(workSchedule));
        when(holidayRepository.existsByStartDateLessThanEqualAndEndDateGreaterThanEqual(any(LocalDate.class))).thenReturn(false);
        when(workScheduleDetailRepository.save(any(WorkScheduleDetail.class))).thenReturn(savedDetail);

        // Act
        workScheduleDetailService.create(createDTO);

        // Assert & Verify
        ArgumentCaptor<WorkScheduleDetail> captor = ArgumentCaptor.forClass(WorkScheduleDetail.class);
        verify(workScheduleDetailRepository, times(1)).save(captor.capture());
        assertTrue(captor.getValue().getIsOvertime(), "Phải là OT khi kết thúc muộn");
    }

    @Test
    @DisplayName("Test create - isOvertime phải là true cho ngày Chủ Nhật")
    void create_ShouldSetOvertimeTrue_ForSunday() {
        // Arrange
        createDTO.setDateWork(LocalDate.of(2024, 8, 11)); // Chủ Nhật
        when(workScheduleRepository.findById(100L)).thenReturn(Optional.of(workSchedule));
        when(holidayRepository.existsByStartDateLessThanEqualAndEndDateGreaterThanEqual(any(LocalDate.class))).thenReturn(false);
        when(workScheduleDetailRepository.save(any(WorkScheduleDetail.class))).thenReturn(savedDetail);

        // Act
        workScheduleDetailService.create(createDTO);

        // Assert & Verify
        ArgumentCaptor<WorkScheduleDetail> captor = ArgumentCaptor.forClass(WorkScheduleDetail.class);
        verify(workScheduleDetailRepository, times(1)).save(captor.capture());
        assertTrue(captor.getValue().getIsOvertime(), "Phải là OT cho ngày Chủ Nhật");
    }

    @Test
    @DisplayName("Test create - isOvertime phải là true cho ngày lễ")
    void create_ShouldSetOvertimeTrue_ForHoliday() {
        // Arrange
        when(workScheduleRepository.findById(100L)).thenReturn(Optional.of(workSchedule));
        when(holidayRepository.existsByStartDateLessThanEqualAndEndDateGreaterThanEqual(createDTO.getDateWork())).thenReturn(true);
        when(workScheduleDetailRepository.save(any(WorkScheduleDetail.class))).thenReturn(savedDetail);

        // Act
        workScheduleDetailService.create(createDTO);

        // Assert & Verify
        ArgumentCaptor<WorkScheduleDetail> captor = ArgumentCaptor.forClass(WorkScheduleDetail.class);
        verify(workScheduleDetailRepository, times(1)).save(captor.capture());
        assertTrue(captor.getValue().getIsOvertime(), "Phải là OT cho ngày lễ");
    }

    @Test
    @DisplayName("Test create - Ném ngoại lệ khi không tìm thấy lịch làm việc")
    void create_ThrowsException_WhenScheduleNotFound() {
        // Arrange
        when(workScheduleRepository.findById(100L)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            workScheduleDetailService.create(createDTO);
        });
        assertEquals("Không tìm thấy lịch làm việc", exception.getMessage());
    }

    @Test
    @DisplayName("Test create - Ném ngoại lệ khi ngày làm việc không khớp với tháng/năm của lịch")
    void create_ThrowsException_WhenDateMismatch() {
        // Arrange
        createDTO.setDateWork(LocalDate.of(2024, 9, 1)); // Tháng 9, không khớp với lịch tháng 8
        when(workScheduleRepository.findById(100L)).thenReturn(Optional.of(workSchedule));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            workScheduleDetailService.create(createDTO);
        });
        assertEquals("Ngày làm việc không nằm trong tháng/năm của lịch làm việc", exception.getMessage());
    }


   
}
