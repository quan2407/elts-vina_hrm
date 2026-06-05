package sep490.com.example.hrms_backend.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import sep490.com.example.hrms_backend.dto.*;
import sep490.com.example.hrms_backend.entity.*;
import sep490.com.example.hrms_backend.mapper.RecruitmentMapper;
import sep490.com.example.hrms_backend.repository.*;
import sep490.com.example.hrms_backend.service.RecruitmentService;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecruitmentServiceTest {

    @Mock private RecruitmentRepository recruitmentRepository;
    @Mock private DepartmentRepository departmentRepository;
    @Mock private AccountRepository accountRepository;

    @InjectMocks private RecruitmentService recruitmentService;

    private Recruitment r(String title, String employmentType, LocalDateTime createdAt, LocalDateTime expiredAt) {
        Recruitment rec = new Recruitment();
        rec.setTitle(title);
        rec.setEmploymentType(employmentType);
        rec.setCreateAt(createdAt);
        rec.setExpiredAt(expiredAt);
        return rec;
    }

    private static LocalDateTime dt(int y, int m, int d, int h) {
        return LocalDateTime.of(y, m, d, h, 0, 0);
    }

    @Test
    void getRecruitmentList_withSearch_sortByTitleAsc_VNFactoryContext() {
        List<Recruitment> data = List.of(
                r("công nhân đóng gói", "Toàn thời gian", dt(2024,1,10,10), dt(2024,2,1,0)),
                r("Công nhân vận hành máy", "Bán thời gian", dt(2024,1,9,9), dt(2024,1,15,0)),
                r("Công nhân kiểm tra chất lượng", "Hợp đồng", dt(2024,1,11,11), dt(2024,3,1,0))
        );
        when(recruitmentRepository.findByTitleContainingIgnoreCase("công"))
                .thenReturn(new ArrayList<>(data));

        try (MockedStatic<RecruitmentMapper> mocked = Mockito.mockStatic(RecruitmentMapper.class)) {
            mocked.when(() -> RecruitmentMapper.mapToRecruitmentDtoList(anyList()))
                    .thenAnswer(inv -> {
                        @SuppressWarnings("unchecked")
                        List<Recruitment> in = (List<Recruitment>) inv.getArgument(0);
                        return in.stream().map(rec -> {
                            RecruitmentDto dto = new RecruitmentDto();
                            dto.setTitle(rec.getTitle());
                            return dto;
                        }).toList();
                    });

            List<RecruitmentDto> result =
                    recruitmentService.getRecruitmentList("công", "title", "asc");

            assertEquals(
                    List.of("Công nhân kiểm tra chất lượng", "Công nhân vận hành máy", "công nhân đóng gói"),
                    result.stream().map(RecruitmentDto::getTitle).toList()
            );

            verify(recruitmentRepository, times(1)).findByTitleContainingIgnoreCase("công");
            verify(recruitmentRepository, never()).findAll();
            verifyNoInteractions(departmentRepository, accountRepository);
        }
    }

    @Test
    void getRecruitmentList_noSearch_defaultSortByCreateAtAsc_VNFactoryContext() {
        List<Recruitment> data = List.of(
                r("Công nhân lắp ráp", "Toàn thời gian", dt(2024,1,10,8), dt(2024,2,28,0)),
                r("Nhân viên kiểm kho", "Bán thời gian", dt(2024,1,11,9), dt(2024,3,15,0)),
                r("Tổ trưởng chuyền", "Toàn thời gian", dt(2024,1,12,10), dt(2024,4,1,0))
        );
        when(recruitmentRepository.findAll()).thenReturn(new ArrayList<>(data));

        try (MockedStatic<RecruitmentMapper> mocked = Mockito.mockStatic(RecruitmentMapper.class)) {
            mocked.when(() -> RecruitmentMapper.mapToRecruitmentDtoList(anyList()))
                    .thenAnswer(inv -> {
                        @SuppressWarnings("unchecked")
                        List<Recruitment> in = (List<Recruitment>) inv.getArgument(0);
                        return in.stream().map(rec -> {
                            RecruitmentDto dto = new RecruitmentDto();
                            dto.setTitle(rec.getTitle());
                            return dto;
                        }).toList();
                    });

            List<RecruitmentDto> result =
                    recruitmentService.getRecruitmentList(null, "", null);

            assertEquals(
                    List.of("Công nhân lắp ráp", "Nhân viên kiểm kho", "Tổ trưởng chuyền"),
                    result.stream().map(RecruitmentDto::getTitle).toList()
            );

            verify(recruitmentRepository, times(1)).findAll();
            verifyNoMoreInteractions(recruitmentRepository);
        }
    }

    @Test
    void getRecruitmentList_sortByEmploymentTypeDesc_VNFactoryContext() {
        List<Recruitment> data = List.of(
                r("Công nhân vận hành máy", "Hợp đồng", dt(2024,1,10,10), dt(2024,2,1,0)),
                r("Nhân viên đóng gói", "Bán thời gian", dt(2024,1,9,9), dt(2024,1,15,0)),
                r("Kỹ thuật viên bảo trì", "Toàn thời gian", dt(2024,1,11,11), dt(2024,3,1,0))
        );
        when(recruitmentRepository.findAll()).thenReturn(new ArrayList<>(data));

        try (MockedStatic<RecruitmentMapper> mocked = Mockito.mockStatic(RecruitmentMapper.class)) {
            mocked.when(() -> RecruitmentMapper.mapToRecruitmentDtoList(anyList()))
                    .thenAnswer(inv -> {
                        @SuppressWarnings("unchecked")
                        List<Recruitment> in = (List<Recruitment>) inv.getArgument(0);
                        return in.stream().map(rec -> {
                            RecruitmentDto dto = new RecruitmentDto();
                            dto.setTitle(rec.getTitle() + " - " + rec.getEmploymentType());
                            return dto;
                        }).toList();
                    });

            List<RecruitmentDto> result =
                    recruitmentService.getRecruitmentList("", "employmentType", "DESC");

            List<String> typesOrder = result.stream()
                    .map(RecruitmentDto::getTitle)
                    .map(s -> s.substring(s.indexOf(" - ") + 3))
                    .toList();
            assertEquals(List.of("Toàn thời gian", "Hợp đồng", "Bán thời gian"), typesOrder);

            verify(recruitmentRepository, times(1)).findAll();
        }
    }

    @Test
    void getRecruitmentList_sortByExpiredAtAsc_VNFactoryContext() {
        List<Recruitment> data = List.of(
                r("Công nhân đóng gói ca đêm", "Toàn thời gian", dt(2024,1,10,10), dt(2024,6,1,0)),
                r("Nhân viên kiểm kho ca ngày", "Toàn thời gian", dt(2024,1,9,9), dt(2024,5,1,0)),
                r("Tổ trưởng chuyền lắp ráp", "Toàn thời gian", dt(2024,1,11,11), dt(2024,7,1,0))
        );
        when(recruitmentRepository.findAll()).thenReturn(new ArrayList<>(data));

        try (MockedStatic<RecruitmentMapper> mocked = Mockito.mockStatic(RecruitmentMapper.class)) {
            mocked.when(() -> RecruitmentMapper.mapToRecruitmentDtoList(anyList()))
                    .thenAnswer(inv -> {
                        @SuppressWarnings("unchecked")
                        List<Recruitment> in = (List<Recruitment>) inv.getArgument(0);
                        return in.stream().map(rec -> {
                            RecruitmentDto dto = new RecruitmentDto();
                            dto.setTitle(rec.getTitle());
                            return dto;
                        }).toList();
                    });

            List<RecruitmentDto> result =
                    recruitmentService.getRecruitmentList(null, "expiredAt", "asc");

            assertEquals(
                    List.of("Nhân viên kiểm kho ca ngày", "Công nhân đóng gói ca đêm", "Tổ trưởng chuyền lắp ráp"),
                    result.stream().map(RecruitmentDto::getTitle).toList()
            );

            verify(recruitmentRepository, times(1)).findAll();
        }
    }
}
