package sep490.com.example.hrms_backend.service.impl;

import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import sep490.com.example.hrms_backend.dto.CandidateResponseDTO;
import sep490.com.example.hrms_backend.entity.CandidateRecruitment;
import sep490.com.example.hrms_backend.mapper.CandidateMapper;
import sep490.com.example.hrms_backend.repository.CandidateRecruitmentRepository;
import sep490.com.example.hrms_backend.service.CandidateService;
@ExtendWith(MockitoExtension.class)
class CandidateServiceTest {

    @Mock private CandidateRecruitmentRepository candidateRecruitmentRepository;

    @InjectMocks private CandidateService candidateService;


    private CandidateRecruitment cr() {
        return new CandidateRecruitment();
    }
    private CandidateResponseDTO dto() {
        return new CandidateResponseDTO();
    }

    /**
     * Trả về danh sách ứng viên theo đúng thứ tự repository trả về
     * và map bằng CandidateMapper (static).
     */
    @Test
    void getCandidatesByRecruitmentId_returnsMappedList_preservesOrder() {
        Long recruitmentId = 100L;


        CandidateRecruitment cr1 = cr(); // Ứng viên "Công nhân đóng gói"
        CandidateRecruitment cr2 = cr(); // Ứng viên "Công nhân vận hành máy"
        CandidateRecruitment cr3 = cr(); // Ứng viên "Kỹ thuật viên bảo trì"

        when(candidateRecruitmentRepository.findByRecruitmentId(recruitmentId))
                .thenReturn(List.of(cr2, cr1, cr3));

        CandidateResponseDTO dto2 = dto(); // tương ứng cr2
        CandidateResponseDTO dto1 = dto(); // tương ứng cr1
        CandidateResponseDTO dto3 = dto(); // tương ứng cr3

        try (MockedStatic<CandidateMapper> mocked = Mockito.mockStatic(CandidateMapper.class)) {
            mocked.when(() -> CandidateMapper.toCandidateResponseDTO(cr2)).thenReturn(dto2);
            mocked.when(() -> CandidateMapper.toCandidateResponseDTO(cr1)).thenReturn(dto1);
            mocked.when(() -> CandidateMapper.toCandidateResponseDTO(cr3)).thenReturn(dto3);

            var result = candidateService.getCandidatesByRecruitmentId(recruitmentId);

            assertIterableEquals(List.of(dto2, dto1, dto3), result);

            verify(candidateRecruitmentRepository, times(1))
                    .findByRecruitmentId(recruitmentId);

            mocked.verify(() -> CandidateMapper.toCandidateResponseDTO(cr2), times(1));
            mocked.verify(() -> CandidateMapper.toCandidateResponseDTO(cr1), times(1));
            mocked.verify(() -> CandidateMapper.toCandidateResponseDTO(cr3), times(1));
        }
    }

    /**
     * Repository trả về rỗng -> kết quả rỗng, mapper không được gọi.
     */
    @Test
    void getCandidatesByRecruitmentId_emptyList_returnsEmpty_andDoesNotMap() {
        Long recruitmentId = 200L;
        when(candidateRecruitmentRepository.findByRecruitmentId(recruitmentId))
                .thenReturn(List.of());

        try (MockedStatic<CandidateMapper> mocked = Mockito.mockStatic(CandidateMapper.class)) {
            var result = candidateService.getCandidatesByRecruitmentId(recruitmentId);

            assertNotNull(result);
            assertTrue(result.isEmpty());

            verify(candidateRecruitmentRepository, times(1))
                    .findByRecruitmentId(recruitmentId);

            mocked.verifyNoInteractions();
        }
    }

    /**
     * Truyền recruitmentId = null (service không validate),
     * repository nhận null và trả rỗng -> service trả rỗng.
     * (Hành vi này tuỳ thuộc cách bạn muốn định nghĩa — nếu cần có thể expect exception.)
     */
    @Test
    void getCandidatesByRecruitmentId_nullId_repoReturnsEmpty_returnsEmpty() {
        when(candidateRecruitmentRepository.findByRecruitmentId(null))
                .thenReturn(List.of());

        try (MockedStatic<CandidateMapper> mocked = Mockito.mockStatic(CandidateMapper.class)) {
            var result = candidateService.getCandidatesByRecruitmentId(null);

            assertNotNull(result);
            assertTrue(result.isEmpty());

            verify(candidateRecruitmentRepository, times(1))
                    .findByRecruitmentId(null);
            mocked.verifyNoInteractions();
        }
    }
}