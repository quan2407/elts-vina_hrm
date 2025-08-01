package sep490.com.example.hrms_backend.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import sep490.com.example.hrms_backend.dto.benefit.BenefitDTO;
import sep490.com.example.hrms_backend.dto.benefit.PatchBenefitDTO;
import sep490.com.example.hrms_backend.entity.Benefit;
import sep490.com.example.hrms_backend.entity.Employee;
import sep490.com.example.hrms_backend.exception.HRMSAPIException;
import sep490.com.example.hrms_backend.mapper.BenefitMapper;
import sep490.com.example.hrms_backend.repository.BenefitRepository;
import sep490.com.example.hrms_backend.repository.EmployeeRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BenefitServiceImplTest {
    @InjectMocks
    private BenefitServiceImpl benefitService;

    @Mock
    private BenefitRepository benefitRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private BenefitMapper benefitMapper;

    //1. addBenefit()
    //1.a. them phuc loi thanh cong
    @Test
    void testAddBenefit_Success() {
        BenefitDTO benefitDTO = new BenefitDTO();
        benefitDTO.setTitle("Du lịch Hè 2025");
        benefitDTO.setMaxParticipants(5);

        Benefit benefitEntity = new Benefit();
        benefitEntity.setTitle("Du lịch Hè 2025");
        benefitEntity.setMaxParticipants(5);

        List<Employee> employees = List.of(new Employee(), new Employee(), new Employee(), new Employee(), new Employee());

        when(modelMapper.map(benefitDTO, Benefit.class)).thenReturn(benefitEntity);
        when(benefitRepository.findByTitle("Du lịch Hè 2025")).thenReturn(null);
        when(employeeRepository.findAllActive()).thenReturn(employees);
        when(benefitRepository.save(any(Benefit.class))).thenReturn(benefitEntity);
        when(modelMapper.map(any(Benefit.class), eq(BenefitDTO.class))).thenAnswer(invocation -> {
            Benefit input = invocation.getArgument(0);
            BenefitDTO result = new BenefitDTO();
            result.setTitle(input.getTitle());
            result.setMaxParticipants(input.getMaxParticipants());
            result.setNumberOfEmployee(employees.size());
            return result;
        });

        BenefitDTO result = benefitService.addBenefit(benefitDTO);

        assertNotNull(result);
        assertEquals("Du lịch Hè 2025", result.getTitle());
        assertEquals(5, result.getMaxParticipants());
        assertEquals(5, result.getNumberOfEmployee());
    }

    //1.b. trung tieu de
    @Test
    void testAddBenefit_Fail_DuplicateTitle() {
        // Arrange
        BenefitDTO benefitDTO = new BenefitDTO();
        benefitDTO.setTitle("tiêu đề trùng");

        Benefit mappedBenefit = new Benefit();
        mappedBenefit.setTitle("tiêu đề trùng");

        when(modelMapper.map(benefitDTO, Benefit.class)).thenReturn(mappedBenefit);
        when(benefitRepository.findByTitle("tiêu đề trùng")).thenReturn(new Benefit());

        // Act + Assert
        HRMSAPIException exception = assertThrows(HRMSAPIException.class,
                () -> benefitService.addBenefit(benefitDTO));

        assertEquals("Benefit with title tiêu đề trùng is already existed.", exception.getMessage());
    }

    //1.c. so luong nguoi tham gia vuot qua nhan vien cong ty
    @Test
    void testAddBenefit_Fail_MaxParticipantsExceedsEmployeeCount() {
        BenefitDTO benefitDTO = new BenefitDTO();
        benefitDTO.setTitle("Du lịch xa");
        benefitDTO.setMaxParticipants(10);

        Benefit mappedBenefit = new Benefit();
        mappedBenefit.setTitle("Du lịch xa");
        mappedBenefit.setMaxParticipants(10);

        List<Employee> employees = List.of(new Employee(), new Employee(), new Employee()); // chỉ có 3 người

        when(modelMapper.map(benefitDTO, Benefit.class)).thenReturn(mappedBenefit);
        when(benefitRepository.findByTitle("Du lịch xa")).thenReturn(null);
        when(employeeRepository.findAllActive()).thenReturn(employees);

        HRMSAPIException exception = assertThrows(HRMSAPIException.class,
                () -> benefitService.addBenefit(benefitDTO));

        assertEquals("Số người tham gia không thể lớn hơn số nhân viên công ty đang làm việc", exception.getMessage());
    }

    //1.d. endDate < startDate
    @Test
    void testAddBenefit_Fail_EndDateBeforeStartDate() {
        BenefitDTO benefitDTO = new BenefitDTO();
        benefitDTO.setTitle("Khuyến mãi ngược thời gian");
        benefitDTO.setMaxParticipants(5);
        benefitDTO.setStartDate(LocalDate.of(2025, 8, 10));
        benefitDTO.setEndDate(LocalDate.of(2025, 8, 5)); // trước startDate

        Benefit mappedBenefit = new Benefit();
        mappedBenefit.setTitle("Khuyến mãi ngược thời gian");
        mappedBenefit.setMaxParticipants(5);
        mappedBenefit.setStartDate(benefitDTO.getStartDate());
        mappedBenefit.setEndDate(benefitDTO.getEndDate());

        when(modelMapper.map(benefitDTO, Benefit.class)).thenReturn(mappedBenefit);

        HRMSAPIException exception = assertThrows(HRMSAPIException.class, () -> {
            benefitService.addBenefit(benefitDTO);
        });

        assertEquals("Ngày kết thúc không được nhỏ hơn ngày bắt đầu.", exception.getMessage());
    }

    //2. updateBenefit
    //2.a.Cập nhật thành công toàn bộ thông tin
    @Test
    void testUpdateBenefit_Success_AllFields() {
        PatchBenefitDTO dto = new PatchBenefitDTO();
        dto.setTitle("New Title");
        dto.setStartDate(LocalDate.of(2025, 8, 1));
        dto.setEndDate(LocalDate.of(2025, 8, 10));
        dto.setMaxParticipants(3);
        dto.setIsActive(true);
        dto.setDescription("Desc");
        dto.setDetail("Detail");

        Benefit benefitFromDb = new Benefit();
        benefitFromDb.setId(1L);

        when(benefitRepository.findById(1L)).thenReturn(Optional.of(benefitFromDb));
        when(employeeRepository.findAllActive()).thenReturn(List.of(new Employee(), new Employee(), new Employee()));
        when(modelMapper.map(dto, Benefit.class)).thenReturn(modelMapper.map(dto, Benefit.class));
        when(modelMapper.map(any(Benefit.class), eq(BenefitDTO.class))).thenReturn(new BenefitDTO());
        when(benefitRepository.save(any(Benefit.class))).thenReturn(benefitFromDb);

        BenefitDTO result = benefitService.updateBenefit(dto, 1L);
        assertNotNull(result);
    }



}
