package sep490.com.example.hrms_backend.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sep490.com.example.hrms_backend.dto.DepartmentDTO;
import sep490.com.example.hrms_backend.dto.LineDTO;
import sep490.com.example.hrms_backend.dto.PositionDTO;
import sep490.com.example.hrms_backend.entity.Department;
import sep490.com.example.hrms_backend.entity.Line;
import sep490.com.example.hrms_backend.entity.Position;
import sep490.com.example.hrms_backend.repository.DepartmentRepository;
import sep490.com.example.hrms_backend.repository.LineRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DepartmentServiceImplTest {

    // 1. Mock các dependency
    @Mock
    private DepartmentRepository departmentRepository;

    @Mock
    private LineRepository lineRepository;

    // 2. Tiêm mock vào service cần test
    @InjectMocks
    private DepartmentServiceImpl departmentService;

    // 3. Khai báo dữ liệu mẫu
    private Department departmentIT;
    private Department departmentHR;
    private Position positionDev;
    private Line lineA;

    // 4. Thiết lập dữ liệu mẫu trước mỗi test
    @BeforeEach
    void setUp() {
        // --- Vị trí ---
        positionDev = new Position();
        positionDev.setPositionId(10L);
        positionDev.setPositionName("Developer");

        // --- Chuyền sản xuất ---
        lineA = new Line();
        lineA.setLineId(100L);
        lineA.setLineName("Chuyền A");

        // --- Phòng ban ---
        departmentIT = new Department();
        departmentIT.setDepartmentId(1L);
        departmentIT.setDepartmentName("Công nghệ thông tin");
        // SỬA LỖI: Thay đổi Set.of() thành List.of()
        departmentIT.setPositions(List.of(positionDev));

        departmentHR = new Department();
        departmentHR.setDepartmentId(2L);
        departmentHR.setDepartmentName("Nhân sự");
        // SỬA LỖI: Thay đổi Collections.emptySet() thành Collections.emptyList()
        departmentHR.setPositions(Collections.emptyList());
    }

    //================================================================================
    // Test cho phương thức getAllDepartments
    //================================================================================

    @Test
    @DisplayName("Test getAllDepartments - Trả về danh sách tất cả phòng ban")
    void getAllDepartments_Success() {
        // Arrange
        when(departmentRepository.findAll()).thenReturn(List.of(departmentIT, departmentHR));

        // Act
        List<DepartmentDTO> result = departmentService.getAllDepartments();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Công nghệ thông tin", result.get(0).getName());
        assertEquals("Nhân sự", result.get(1).getName());

        // Verify
        verify(departmentRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Test getAllDepartments - Trả về danh sách rỗng khi không có phòng ban nào")
    void getAllDepartments_ReturnsEmptyList() {
        // Arrange
        when(departmentRepository.findAll()).thenReturn(Collections.emptyList());

        // Act
        List<DepartmentDTO> result = departmentService.getAllDepartments();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    //================================================================================
    // Test cho phương thức getPositionsByDepartment
    //================================================================================

    @Test
    @DisplayName("Test getPositionsByDepartment - Trả về danh sách vị trí của một phòng ban")
    void getPositionsByDepartment_Success() {
        // Arrange
        Long departmentId = 1L;
        when(departmentRepository.findById(departmentId)).thenReturn(Optional.of(departmentIT));

        // Act
        List<PositionDTO> result = departmentService.getPositionsByDepartment(departmentId);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Developer", result.get(0).getName());
    }

    @Test
    @DisplayName("Test getPositionsByDepartment - Ném ngoại lệ khi không tìm thấy phòng ban")
    void getPositionsByDepartment_ThrowsException_WhenDepartmentNotFound() {
        // Arrange
        Long nonExistentId = 99L;
        when(departmentRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            departmentService.getPositionsByDepartment(nonExistentId);
        });
        assertEquals("Department not found with id: " + nonExistentId, exception.getMessage());
    }

    //================================================================================
    // Test cho phương thức getLinesByDepartment
    //================================================================================

    @Test
    @DisplayName("Test getLinesByDepartment - Trả về danh sách chuyền của một phòng ban")
    void getLinesByDepartment_Success() {
        // Arrange
        Long departmentId = 1L;
        when(lineRepository.findByDepartmentDepartmentId(departmentId)).thenReturn(List.of(lineA));

        // Act
        List<LineDTO> result = departmentService.getLinesByDepartment(departmentId);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Chuyền A", result.get(0).getName());
    }

    @Test
    @DisplayName("Test getLinesByDepartment - Trả về danh sách rỗng khi phòng ban không có chuyền")
    void getLinesByDepartment_ReturnsEmptyList() {
        // Arrange
        Long departmentId = 2L; // Phòng Nhân sự không có chuyền
        when(lineRepository.findByDepartmentDepartmentId(departmentId)).thenReturn(Collections.emptyList());

        // Act
        List<LineDTO> result = departmentService.getLinesByDepartment(departmentId);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}
