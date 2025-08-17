package sep490.com.example.hrms_backend.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import sep490.com.example.hrms_backend.dto.PositionDTO;
import sep490.com.example.hrms_backend.entity.Position;
import sep490.com.example.hrms_backend.exception.HRMSAPIException;
import sep490.com.example.hrms_backend.repository.BenefitPositionRepository;
import sep490.com.example.hrms_backend.repository.PositionRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PositionServiceImplTest {
    // 1. Mock các dependency
    @Mock
    private PositionRepository positionRepository;

    @Mock
    private BenefitPositionRepository benefitPositionRepository;

    @Mock
    private ModelMapper modelMapper;

    // 2. Tiêm mock vào service cần test
    @InjectMocks
    private PositionServiceImpl positionService;

    // 3. Khai báo dữ liệu mẫu
    private Position positionCongNhan, positionKiemTra, positionVeSinh;
    private PositionDTO positionCongNhanDTO;

    // 4. Thiết lập dữ liệu mẫu trước mỗi test
    @BeforeEach
    void setUp() {
        positionCongNhan = new Position();
        positionCongNhan.setPositionId(1L);
        positionCongNhan.setPositionName("Công nhân");

        positionKiemTra = new Position();
        positionKiemTra.setPositionId(2L);
        positionKiemTra.setPositionName("Công nhân kiểm tra");

        positionVeSinh = new Position();
        positionVeSinh.setPositionId(3L);
        positionVeSinh.setPositionName("Nhân viên vệ sinh");

        positionCongNhanDTO = new PositionDTO();
        positionCongNhanDTO.setId(1L);
        positionCongNhanDTO.setName("Công nhân");
    }

    //================================================================================
    // Test cho phương thức getPositionsNotRegisteredToBenefit
    //================================================================================

    @Test
    @DisplayName("Test getPositionsNotRegisteredToBenefit - Trả về các vị trí chưa đăng ký")
    void getPositionsNotRegisteredToBenefit_Success_WithRegisteredPositions() {
        // Arrange
        Long benefitId = 100L;
        // Giả sử vị trí Công nhân (ID 1) đã đăng ký
        List<Long> registeredPositionIds = List.of(1L);
        // Các vị trí còn lại là Công nhân kiểm tra và Nhân viên vệ sinh
        List<Position> availablePositions = List.of(positionKiemTra, positionVeSinh);

        when(benefitPositionRepository.findPositionIdsByBenefitId(benefitId)).thenReturn(registeredPositionIds);
        when(positionRepository.findByPositionIdNotIn(registeredPositionIds)).thenReturn(availablePositions);
        // Giả lập model mapper
        when(modelMapper.map(any(Position.class), eq(PositionDTO.class)))
                .thenAnswer(invocation -> {
                    Position p = invocation.getArgument(0);
                    return new PositionDTO(p.getPositionId(), p.getPositionName());
                });

        // Act
        List<PositionDTO> result = positionService.getPositionsNotRegisteredToBenefit(benefitId);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(p -> p.getName().equals("Công nhân kiểm tra")));
        assertTrue(result.stream().anyMatch(p -> p.getName().equals("Nhân viên vệ sinh")));
        assertFalse(result.stream().anyMatch(p -> p.getName().equals("Công nhân")));

        // Verify
        verify(positionRepository, times(1)).findByPositionIdNotIn(registeredPositionIds);
        verify(positionRepository, never()).findAll();
    }

    @Test
    @DisplayName("Test getPositionsNotRegisteredToBenefit - Trả về tất cả vị trí khi chưa có vị trí nào đăng ký")
    void getPositionsNotRegisteredToBenefit_Success_WithNoRegisteredPositions() {
        // Arrange
        Long benefitId = 100L;
        List<Long> registeredPositionIds = Collections.emptyList();
        List<Position> allPositions = List.of(positionCongNhan, positionKiemTra, positionVeSinh);

        when(benefitPositionRepository.findPositionIdsByBenefitId(benefitId)).thenReturn(registeredPositionIds);
        when(positionRepository.findAll()).thenReturn(allPositions);

        // Act
        positionService.getPositionsNotRegisteredToBenefit(benefitId);

        // Assert & Verify
        // Kiểm tra xem phương thức findAll() có được gọi không
        verify(positionRepository, times(1)).findAll();
        // Kiểm tra xem phương thức findByPositionIdNotIn() không được gọi
        verify(positionRepository, never()).findByPositionIdNotIn(anyList());
    }

    //================================================================================
    // Test cho phương thức getPositionById
    //================================================================================

    @Test
    @DisplayName("Test getPositionById - Lấy vị trí thành công")
    void getPositionById_Success() {
        // Arrange
        Long positionId = 1L;
        when(positionRepository.findById(positionId)).thenReturn(Optional.of(positionCongNhan));
        when(modelMapper.map(positionCongNhan, PositionDTO.class)).thenReturn(positionCongNhanDTO);

        // Act
        PositionDTO result = positionService.getPositionById(positionId);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Công nhân", result.getName());
    }

    @Test
    @DisplayName("Test getPositionById - Ném ngoại lệ khi không tìm thấy vị trí")
    void getPositionById_ThrowsException_WhenNotFound() {
        // Arrange
        Long nonExistentId = 99L;
        when(positionRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // Act & Assert
        HRMSAPIException exception = assertThrows(HRMSAPIException.class, () -> {
            positionService.getPositionById(nonExistentId);
        });
        assertEquals("Vị trí với id " + nonExistentId + " không tồn tại.", exception.getMessage());
    }
}
