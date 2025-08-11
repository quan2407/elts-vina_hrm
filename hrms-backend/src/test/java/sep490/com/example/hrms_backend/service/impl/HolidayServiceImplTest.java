package sep490.com.example.hrms_backend.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import sep490.com.example.hrms_backend.dto.HolidayDTO;
import sep490.com.example.hrms_backend.entity.Holiday;
import sep490.com.example.hrms_backend.mapper.HolidayMapper;
import sep490.com.example.hrms_backend.repository.HolidayRepository;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class HolidayServiceImplTest {

    // 1. Mock dependency
    @Mock
    private HolidayRepository holidayRepository;

    // 2. Tiêm mock vào service cần test
    @InjectMocks
    private HolidayServiceImpl holidayService;

    // 3. Khai báo dữ liệu mẫu
    private Holiday holidayOneTime;
    private Holiday holidayRecurring;
    private Holiday holidayDeleted;
    private HolidayDTO holidayDTO;

    // 4. Thiết lập dữ liệu mẫu trước mỗi test
    @BeforeEach
    void setUp() {
        holidayOneTime = new Holiday();
        holidayOneTime.setId(1L);
        holidayOneTime.setName("Giải phóng miền Nam");
        holidayOneTime.setStartDate(LocalDate.of(2024, 4, 30));
        holidayOneTime.setEndDate(LocalDate.of(2024, 4, 30));
        holidayOneTime.setRecurring(false);
        holidayOneTime.setDeleted(false);

        holidayRecurring = new Holiday();
        holidayRecurring.setId(2L);
        holidayRecurring.setName("Tết Dương lịch");
        // Năm không quan trọng với ngày nghỉ lặp lại
        holidayRecurring.setStartDate(LocalDate.of(2000, 1, 1));
        holidayRecurring.setEndDate(LocalDate.of(2000, 1, 1));
        holidayRecurring.setRecurring(true);
        holidayRecurring.setDeleted(false);

        holidayDeleted = new Holiday();
        holidayDeleted.setId(3L);
        holidayDeleted.setName("Ngày nghỉ đã xóa");
        holidayDeleted.setDeleted(true);

        holidayDTO = new HolidayDTO();
        holidayDTO.setId(1L);
        holidayDTO.setName("Giải phóng miền Nam");
    }

    //================================================================================
    // Test cho phương thức getAllHolidays
    //================================================================================

    @Test
    @DisplayName("Test getAllHolidays - Trả về danh sách ngày nghỉ chưa bị xóa")
    void getAllHolidays_ShouldReturnOnlyNonDeleted() {
        // Arrange
        when(holidayRepository.findAll()).thenReturn(List.of(holidayOneTime, holidayRecurring, holidayDeleted));

        // Act
        // Dùng try-with-resources để mock static mapper
        try (MockedStatic<HolidayMapper> mockedMapper = mockStatic(HolidayMapper.class)) {
            mockedMapper.when(() -> HolidayMapper.mapToDTO(any(Holiday.class)))
                    .thenAnswer(invocation -> {
                        // SỬA LỖI: Ép kiểu đối tượng về Holiday trước khi gọi getId()
                        Holiday holiday = (Holiday) invocation.getArgument(0);
                        return new HolidayDTO(holiday.getId(), null, null, null, false);
                    });

            List<HolidayDTO> result = holidayService.getAllHolidays();

            // Assert
            assertNotNull(result);
            assertEquals(2, result.size(), "Chỉ có 2 ngày nghỉ không bị xóa được trả về");
            assertTrue(result.stream().anyMatch(h -> h.getId().equals(1L)));
            assertTrue(result.stream().anyMatch(h -> h.getId().equals(2L)));
        }
    }

    //================================================================================
    // Test cho phương thức createHoliday
    //================================================================================

    @Test
    @DisplayName("Test createHoliday - Tạo ngày nghỉ thành công")
    void createHoliday_Success() {
        // Arrange
        when(holidayRepository.save(any(Holiday.class))).thenReturn(holidayOneTime);

        // Act
        try (MockedStatic<HolidayMapper> mockedMapper = mockStatic(HolidayMapper.class)) {
            mockedMapper.when(() -> HolidayMapper.mapToEntity(any(HolidayDTO.class))).thenReturn(new Holiday());
            mockedMapper.when(() -> HolidayMapper.mapToDTO(any(Holiday.class))).thenReturn(holidayDTO);

            ArgumentCaptor<Holiday> captor = ArgumentCaptor.forClass(Holiday.class);
            HolidayDTO result = holidayService.createHoliday(new HolidayDTO());

            // Assert
            assertNotNull(result);
            assertEquals(1L, result.getId());

            // Verify
            verify(holidayRepository).save(captor.capture());
            assertFalse(captor.getValue().isDeleted(), "Ngày nghỉ mới tạo phải có isDeleted = false");
        }
    }

    //================================================================================
    // Test cho phương thức getHolidayById
    //================================================================================

    @Test
    @DisplayName("Test getHolidayById - Lấy thành công khi ID tồn tại")
    void getHolidayById_Success() {
        // Arrange
        when(holidayRepository.findByIdAndIsDeletedFalse(1L)).thenReturn(Optional.of(holidayOneTime));

        // Act
        try (MockedStatic<HolidayMapper> mockedMapper = mockStatic(HolidayMapper.class)) {
            mockedMapper.when(() -> HolidayMapper.mapToDTO(any(Holiday.class))).thenReturn(holidayDTO);
            HolidayDTO result = holidayService.getHolidayById(1L);
            // Assert
            assertEquals(holidayDTO.getId(), result.getId());
        }
    }

    @Test
    @DisplayName("Test getHolidayById - Ném ngoại lệ khi ID không tồn tại")
    void getHolidayById_ThrowsException_WhenNotFound() {
        // Arrange
        when(holidayRepository.findByIdAndIsDeletedFalse(99L)).thenReturn(Optional.empty());
        // Act & Assert
        assertThrows(RuntimeException.class, () -> holidayService.getHolidayById(99L));
    }

    //================================================================================
    // Test cho phương thức updateHoliday
    //================================================================================


    @Test
    @DisplayName("Test updateHoliday - Cập nhật thành công")
    void updateHoliday_Success() {
        // Arrange
        HolidayDTO updateInfo = new HolidayDTO();
        updateInfo.setName("Tên Mới");

        when(holidayRepository.findByIdAndIsDeletedFalse(1L)).thenReturn(Optional.of(holidayOneTime));
        when(holidayRepository.save(any(Holiday.class))).thenReturn(holidayOneTime);

        // Act
        try (MockedStatic<HolidayMapper> mockedMapper = mockStatic(HolidayMapper.class)) {
            // Giả lập việc mapper cập nhật tên
            mockedMapper.when(() -> HolidayMapper.updateHolidayFromDTO(any(), any()))
                    .thenAnswer(invocation -> {
                        Holiday h = invocation.getArgument(1);
                        h.setName("Tên Mới");
                        return null;
                    });
            mockedMapper.when(() -> HolidayMapper.mapToDTO(any(Holiday.class))).thenReturn(updateInfo);

            holidayService.updateHoliday(1L, updateInfo);

            // Verify
            ArgumentCaptor<Holiday> captor = ArgumentCaptor.forClass(Holiday.class);
            verify(holidayRepository).save(captor.capture());
            assertEquals("Tên Mới", captor.getValue().getName());
        }
    }

    //================================================================================
    // Test cho phương thức softDeleteHoliday
    //================================================================================

    @Test
    @DisplayName("Test softDeleteHoliday - Xóa mềm thành công")
    void softDeleteHoliday_Success() {
        // Arrange
        when(holidayRepository.findById(1L)).thenReturn(Optional.of(holidayOneTime));

        // Act
        holidayService.softDeleteHoliday(1L);

        // Verify
        ArgumentCaptor<Holiday> captor = ArgumentCaptor.forClass(Holiday.class);
        verify(holidayRepository).save(captor.capture());
        assertTrue(captor.getValue().isDeleted(), "Trường isDeleted phải là true sau khi xóa mềm");
    }

    //================================================================================
    // Test cho phương thức isHoliday
    //================================================================================

    @Test
    @DisplayName("Test isHoliday - Trả về true cho ngày nghỉ một lần")
    void isHoliday_ReturnsTrue_ForOneTimeHoliday() {
        // Arrange
        LocalDate date = LocalDate.of(2024, 4, 30);
        when(holidayRepository.existsByStartDateLessThanEqualAndEndDateGreaterThanEqual(date)).thenReturn(true);

        // Act
        boolean result = holidayService.isHoliday(date);

        // Assert
        assertTrue(result);
    }

    @Test
    @DisplayName("Test isHoliday - Trả về true cho ngày nghỉ lặp lại")
    void isHoliday_ReturnsTrue_ForRecurringHoliday() {
        // Arrange
        LocalDate date = LocalDate.of(2024, 1, 1);
        when(holidayRepository.existsByStartDateLessThanEqualAndEndDateGreaterThanEqual(date)).thenReturn(false);
        when(holidayRepository.findAll()).thenReturn(List.of(holidayRecurring));

        // Act
        boolean result = holidayService.isHoliday(date);

        // Assert
        assertTrue(result);
    }

    @Test
    @DisplayName("Test isHoliday - Trả về false cho ngày thường")
    void isHoliday_ReturnsFalse_ForWorkday() {
        // Arrange
        LocalDate date = LocalDate.of(2024, 8, 12);
        when(holidayRepository.existsByStartDateLessThanEqualAndEndDateGreaterThanEqual(date)).thenReturn(false);
        when(holidayRepository.findAll()).thenReturn(Collections.emptyList());

        // Act
        boolean result = holidayService.isHoliday(date);

        // Assert
        assertFalse(result);
    }
}
