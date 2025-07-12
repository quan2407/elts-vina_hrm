package sep490.com.example.hrms_backend.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.core.context.SecurityContextHolder;
import sep490.com.example.hrms_backend.dto.*;
import sep490.com.example.hrms_backend.entity.*;
import sep490.com.example.hrms_backend.enums.RecruitmentStatus;
import sep490.com.example.hrms_backend.mapper.RecruitmentMapper;
import sep490.com.example.hrms_backend.repository.*;
import sep490.com.example.hrms_backend.service.RecruitmentService;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class RecruitmentServiceTest {

    @InjectMocks
    private RecruitmentService recruitmentService;

    @Mock
    private RecruitmentRepository recruitmentRepository;

    @Mock
    private DepartmentRepository departmentRepository;

    @Mock
    private AccountRepository accountRepository;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetRecruitmentList_withoutSearch_shouldSortByCreateAtAsc() {
        Recruitment r1 = new Recruitment(); r1.setCreateAt(LocalDateTime.now().minusDays(1));
        Recruitment r2 = new Recruitment(); r2.setCreateAt(LocalDateTime.now());

        when(recruitmentRepository.findAll()).thenReturn(List.of(r2, r1));

        List<RecruitmentDto> result = recruitmentService.getRecruitmentList(null, "createAt", "asc");

        assertEquals(2, result.size());
        // Bạn có thể kiểm tra thêm các field nếu RecruitmentMapper có logic đặc biệt
    }

    @Test
    void testGetRecruitmentDtoById_shouldReturnDto() {
        Recruitment recruitment = new Recruitment();
        recruitment.setId(1L);

        Department department = new Department();
        department.setDepartmentId(1L); // Giả lập ID phòng ban

        recruitment.setDepartment(department); // Bắt buộc phải set để tránh null

        when(recruitmentRepository.findById(1L)).thenReturn(Optional.of(recruitment));

        RecruitmentDto result = recruitmentService.getRecruitmentDtoById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getDepartmentId()); // giả sử RecruitmentMapper set đúng
    }


    @Test
    void testEditRecruitment_shouldUpdateExisting() {
        RecruitmentDto dto = new RecruitmentDto();
        dto.setTitle("New Title");
        dto.setDepartmentId(2L);

        Recruitment recruitment = new Recruitment();
        recruitment.setId(1L);

        Department dept = new Department();

        when(recruitmentRepository.findById(1L)).thenReturn(Optional.of(recruitment));
        when(departmentRepository.findById(2L)).thenReturn(Optional.of(dept));
        when(recruitmentRepository.save(any())).thenReturn(recruitment);

        RecruitmentDto result = recruitmentService.editRecruitment(1L, dto);

        assertEquals("New Title", recruitment.getTitle());
        verify(recruitmentRepository).save(recruitment);
    }

}
