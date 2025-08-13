package sep490.com.example.hrms_backend.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import sep490.com.example.hrms_backend.dto.benefit.BenefitDTO;
import sep490.com.example.hrms_backend.dto.benefit.BenefitResponse;
import sep490.com.example.hrms_backend.dto.benefit.PatchBenefitDTO;
import sep490.com.example.hrms_backend.entity.Benefit;
import sep490.com.example.hrms_backend.entity.BenefitPosition;
import sep490.com.example.hrms_backend.entity.Employee;
import sep490.com.example.hrms_backend.entity.Position;
import sep490.com.example.hrms_backend.exception.HRMSAPIException;
import sep490.com.example.hrms_backend.exception.ResourceNotFoundException;
import sep490.com.example.hrms_backend.mapper.BenefitMapper;
import sep490.com.example.hrms_backend.repository.BenefitRepository;
import sep490.com.example.hrms_backend.repository.EmployeeRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

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


    private Benefit benefit;
    private BenefitDTO benefitDTO;
    private Position positionDev, positionTester;
    private BenefitPosition benefitPositionDev, benefitPositionTester;

    @BeforeEach
    void setUp() {
        // Thiết lập dữ liệu mẫu sẽ được dùng trong nhiều test case
        benefit = new Benefit();
        benefit.setId(1L);
        benefit.setTitle("Du lịch công ty 2025");
        benefit.setDescription("Du lịch công ty thường niên - Đà Nẵng.");
//        benefit.setMaxParticipants(100);
        benefit.setIsActive(true);

        benefitDTO = new BenefitDTO();
        benefitDTO.setId(1L);
        benefitDTO.setTitle("Du lịch công ty 2025");

        // Tạo các đối tượng mẫu cho Position
        positionDev = new Position();
        positionDev.setPositionId(10L);
        positionDev.setPositionName("Công nhân");

        positionTester = new Position();
        positionTester.setPositionId(20L);
        positionTester.setPositionName("Data");

        // Tạo các đối tượng mẫu cho BenefitPosition
        benefitPositionDev = new BenefitPosition();
        benefitPositionDev.setId(100L);
        benefitPositionDev.setPosition(positionDev);
        benefitPositionDev.setBenefit(benefit); // Gán vào benefit chung

        benefitPositionTester = new BenefitPosition();
        benefitPositionTester.setId(101L);
        benefitPositionTester.setPosition(positionTester);
        benefitPositionTester.setBenefit(benefit); // Gán vào benefit chung

        // Thiết lập cho đối tượng benefit có nhiều benefitPositions
        benefit.setBenefitPositions(new ArrayList<>(List.of(benefitPositionDev, benefitPositionTester)));
    }

    //@@@@@@Test getAllBenefitsForHr()@@@@@@@
    // ######SUCCESS CASE########
    @Test
    @DisplayName("Test getAllBenefitsForHr - Trường hợp thành công, trả về danh sách phúc lợi")
    void testGetAllBenefitsForHr_Success() {
        //1. Arrange
        Integer pageNumber = 1;
        Integer pageSize = 10;
        String sortBy = "startDate";
        String sortOrder = "desc";
        String title = "Du lịch";

        // Tạo một Page giả lập chứa dữ liệu phúc lợi
        List<Benefit> benefitList = Collections.singletonList(benefit);
        Page<Benefit> benefitPage = new PageImpl<>(benefitList, PageRequest.of(0, 10), 1);

        // Tạo danh sách DTO tương ứng
        List<BenefitDTO> benefitDTOList = Collections.singletonList(benefitDTO);

        // Định nghĩa hành vi của mock: khi repository được gọi với bất kỳ Specification và Pageable nào,
        // nó sẽ trả về trang benefitPage đã tạo ở trên.
        when(benefitRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(benefitPage);

        // Định nghĩa hành vi của mock: khi mapper được gọi để chuyển đổi list,
        // nó sẽ trả về list DTO đã tạo.
        when(benefitMapper.toBenefitDTOs(benefitList)).thenReturn(benefitDTOList);


        //2. Act
        // Gọi phương thức cần test
        BenefitResponse response = benefitService.getAllBenefitsForHr(
                "hr_user", pageNumber, pageSize, sortBy, sortOrder,
                title, null, null, null
        );

        //3. Assert
        assertNotNull(response); // Kiểm tra response không null
        assertEquals(1, response.getContent().size()); // Kiểm tra số lượng item trong content
        assertEquals("Du lịch công ty 2025", response.getContent().get(0).getTitle()); // Kiểm tra nội dung
        assertEquals(0, response.getPageNumber()); // Page number trong Spring Data JPA bắt đầu từ 0
        assertEquals(1, response.getTotalPages());
        assertEquals(1L, response.getTotalElements());
        assertTrue(response.isLastPage());

        // 4. Verify
        // Kiểm tra xem phương thức findAll của repository có được gọi đúng 1 lần không
        verify(benefitRepository, times(1)).findAll(any(Specification.class), any(Pageable.class));
        // Kiểm tra xem phương thức toBenefitDTOs của mapper có được gọi đúng 1 lần không
        verify(benefitMapper, times(1)).toBenefitDTOs(benefitList);
    }


    // ######FAILED CASE########
    @Test
    @DisplayName("Test getAllBenefitsForHr - Ném ra ngoại lệ khi không tìm thấy phúc lợi nào")
    void testGetAllBenefitsForHr_ThrowsException_WhenNoBenefitsFound() {
        // 1. Arrange
        // Tạo một Page rỗng
        Page<Benefit> emptyPage = Page.empty();

        // Định nghĩa hành vi của mock: khi repository được gọi, trả về page rỗng
        when(benefitRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(emptyPage);

        // 2. Act & Assert
        // Dùng assertThrows để kiểm tra rằng một ngoại lệ cụ thể được ném ra
        HRMSAPIException exception = assertThrows(HRMSAPIException.class, () -> {
            benefitService.getAllBenefitsForHr("hr_user", 1, 10, "id", "asc", "NonExistent", null, null, null);
        });

        // Kiểm tra thông điệp của ngoại lệ
        assertEquals("Không có phúc lợi nào tương ứng", exception.getMessage());

        //3.  Verify
        // Đảm bảo rằng mapper không bao giờ được gọi vì không có dữ liệu để map
        verify(benefitMapper, never()).toBenefitDTOs(any());
    }

    @Test
    @DisplayName("Test getAllBenefitsForHr - Ném ra ngoại lệ khi ngày kết thúc trước ngày bắt đầu")
    void testGetAllBenefitsForHr_ThrowsException_WhenEndDateIsBeforeStartDate() {
        // 1. Arrange
        LocalDate startDate = LocalDate.of(2025, 1, 10);
        LocalDate endDate = LocalDate.of(2025, 1, 1); // Ngày kết thúc không hợp lệ

        // 2. Act & Assert
        HRMSAPIException exception = assertThrows(HRMSAPIException.class, () -> {
            benefitService.getAllBenefitsForHr("hr_user", 1, 10, "id", "asc", null, null, null, null);
        });

        assertEquals("Ngày kết thúc phải lớn hơn ngày bắt đầu", exception.getMessage());

        //3.  Verify
        // Đảm bảo rằng repository không bao giờ được gọi vì validation đã thất bại trước đó
        verify(benefitRepository, never()).findAll(any(Specification.class), any(Pageable.class));
    }



    //@@@@@@Test addBenefits()@@@@@@@
    // ######SUCCESS CASE########
    @Test
    @DisplayName("Test addBenefit - Thêm phúc lợi thành công")
    void testAddBenefit_Success() {
        // 1. Arrange
        // 1.1. Dữ liệu đầu vào
        BenefitDTO benefitDTO = new BenefitDTO();
        benefitDTO.setTitle("Tiệc năm mới 2026");



        // 1.2. Dữ liệu mock
        Benefit benefitToSave = new Benefit(); // Đối tượng sau khi map
        benefitToSave.setTitle("New Year Party 2026");

        Benefit savedBenefit = new Benefit(); // Đối tượng sau khi lưu vào DB
        savedBenefit.setId(100L);
        savedBenefit.setTitle("New Year Party 2026");

        BenefitDTO finalBenefitDTO = new BenefitDTO(); // DTO trả về cuối cùng
        finalBenefitDTO.setId(100L);
        finalBenefitDTO.setTitle("New Year Party 2026");

        // Giả lập có 80 nhân viên trong công ty
        List<Employee> employees = new ArrayList<>();
        IntStream.range(0, 80).forEach(i -> employees.add(new Employee()));
        when(employeeRepository.findAllActive()).thenReturn(employees);

        // 1.3. Định nghĩa hành vi của mock
        when(modelMapper.map(benefitDTO, Benefit.class)).thenReturn(benefitToSave);
        when(benefitRepository.findByTitle("New Year Party 2026")).thenReturn(null); // Chưa tồn tại
        when(benefitRepository.save(any(Benefit.class))).thenReturn(savedBenefit);
        when(modelMapper.map(savedBenefit, BenefitDTO.class)).thenReturn(finalBenefitDTO);

        // 2.Act
        BenefitDTO result = benefitService.addBenefit(benefitDTO);

        // 3. Assert
        assertNotNull(result);
        assertEquals(100L, result.getId());
        assertEquals("New Year Party 2026", result.getTitle());
        assertEquals(80, result.getNumberOfEmployee()); // Kiểm tra số nhân viên được gán

        //4. Verify
        verify(benefitRepository, times(1)).findByTitle("New Year Party 2026");
        verify(benefitRepository, times(1)).save(any(Benefit.class));
        verify(modelMapper, times(2)).map(any(), any()); // Được gọi 2 lần
    }

    // ######FAILED CASE########
    @Test
    @DisplayName("Test addBenefit - Ném ngoại lệ khi Title đã tồn tại")
    void testAddBenefit_ThrowsException_WhenTitleExists() {
        //1. Arrange
        BenefitDTO benefitDTO = new BenefitDTO();
        benefitDTO.setTitle("Tiêu đề trùng");

        Benefit benefitToSave = new Benefit();
        benefitToSave.setTitle("Tiêu đề trùng");

        Benefit existingBenefitFromDb = new Benefit(); // Phúc lợi đã có trong DB
        existingBenefitFromDb.setId(1L);
        existingBenefitFromDb.setTitle("Tiêu đề trùng");

        when(employeeRepository.findAllActive()).thenReturn(List.of(new Employee()));
        when(modelMapper.map(benefitDTO, Benefit.class)).thenReturn(benefitToSave);
        // Giả lập rằng phúc lợi đã tồn tại
        when(benefitRepository.findByTitle("Tiêu đề trùng")).thenReturn(existingBenefitFromDb);

        // 2.Act & Assert
        HRMSAPIException exception = assertThrows(HRMSAPIException.class, () -> {
            benefitService.addBenefit(benefitDTO);
        });

        assertEquals("Phúc lợi với tiêu đề Tiêu đề trùng đã tồn tại.", exception.getMessage());

        // 3. Verify
        // Đảm bảo phương thức save không bao giờ được gọi
        verify(benefitRepository, never()).save(any(Benefit.class));
    }





    //@@@@@@Test updateBenefit()@@@@@@@
    // ######SUCCESS CASE########


    @Test
    @DisplayName("Test updateBenefit - Cập nhật một phần thành công")
    void testUpdateBenefit_Success_PartialUpdate() {
        // Arrange
        // 1. Dữ liệu đầu vào: Chỉ cập nhật title và maxParticipants
        PatchBenefitDTO patchDTO = new PatchBenefitDTO();
        patchDTO.setTitle("Du lịch công ty MỚI 2025");


        Benefit mappedFromPatch = new Benefit();
        mappedFromPatch.setTitle("Du lịch công ty MỚI 2025");
        when(modelMapper.map(patchDTO, Benefit.class)).thenReturn(mappedFromPatch);

        // Các trường khác trong patchDTO là null

        Long benefitId = 1L;

        // 2. Dữ liệu mock
        // Tạo một bản sao của benefit từ @BeforeEach để tránh thay đổi trạng thái chung
        Benefit benefitFromDb = new Benefit();
        benefitFromDb.setId(benefit.getId());
        benefitFromDb.setTitle(benefit.getTitle());
        benefitFromDb.setDescription(benefit.getDescription());

        benefitFromDb.setIsActive(benefit.getIsActive());


        // Giả lập có 150 nhân viên
        // SỬA LỖI: Dùng stream để tạo 150 đối tượng Employee thật
        List<Employee> mockEmployees = IntStream.range(0, 150)
                .mapToObj(i -> new Employee())
                .collect(Collectors.toList());
        when(employeeRepository.findAllActive()).thenReturn(mockEmployees);
        when(benefitRepository.findById(benefitId)).thenReturn(Optional.of(benefitFromDb));
        // Dùng `thenAnswer` để trả về chính đối tượng được truyền vào save
        when(benefitRepository.save(any(Benefit.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(modelMapper.map(any(Benefit.class), eq(BenefitDTO.class))).thenAnswer(invocation -> {
            Benefit b = invocation.getArgument(0);
            BenefitDTO dto = new BenefitDTO();
            dto.setId(b.getId());
            dto.setTitle(b.getTitle());
            dto.setDescription(b.getDescription()); // Giả lập mapper
            return dto;
        });
        // Không cần mock `modelMapper.map(patchDTO, ...)` vì code không dùng kết quả của nó,
        // mà chỉ dùng để kiểm tra các trường null/not-null.

        // Tạo ArgumentCaptor để "bắt" đối tượng Benefit được truyền vào hàm save
        ArgumentCaptor<Benefit> benefitCaptor = ArgumentCaptor.forClass(Benefit.class);

        // Act
        BenefitDTO updatedDto = benefitService.updateBenefit(patchDTO, benefitId);

        // Assert
        // 1. Kiểm tra DTO trả về
        assertNotNull(updatedDto);
        assertEquals("Du lịch công ty MỚI 2025", updatedDto.getTitle());

        // 2. Dùng captor để kiểm tra đối tượng đã được lưu vào DB
        verify(benefitRepository).save(benefitCaptor.capture());
        Benefit savedBenefit = benefitCaptor.getValue();

        // Kiểm tra các trường đã được cập nhật
        assertEquals("Du lịch công ty MỚI 2025", savedBenefit.getTitle());

        // Quan trọng: Kiểm tra các trường khác KHÔNG bị thay đổi
        assertEquals(benefit.getDescription(), savedBenefit.getDescription());
        assertTrue(savedBenefit.getIsActive());
    }

    //@@@@@@Test updateBenefit()@@@@@@@
    // ######FAILED CASE########

    @Test
    @DisplayName("Test updateBenefit - Ném ngoại lệ khi không tìm thấy Benefit ID")
    void testUpdateBenefit_ThrowsException_WhenBenefitNotFound() {
        // Arrange
        Long nonExistentId = 99L;
        PatchBenefitDTO patchDTO = new PatchBenefitDTO();
        when(benefitRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // Act & Assert
        HRMSAPIException exception = assertThrows(HRMSAPIException.class, () -> {
            benefitService.updateBenefit(patchDTO, nonExistentId);
        });

        assertEquals("Benefit with id " + nonExistentId + " is not existed.", exception.getMessage());
        verify(benefitRepository, never()).save(any());
    }



    @Test
    @DisplayName("Test updateBenefit - Ném ngoại lệ khi ngày kết thúc trước ngày bắt đầu")
    void testUpdateBenefit_ThrowsException_WhenEndDateIsBeforeStartDate() {
        // Arrange
        PatchBenefitDTO patchDTO = new PatchBenefitDTO();
        patchDTO.setEndDate(LocalDate.of(2025, 8, 31)); // Ngày kết thúc mới < ngày bắt đầu cũ
        Long benefitId = 1L;

        // Dữ liệu từ DB có benefit.getStartDate() là 2025-09-01
        when(benefitRepository.findById(benefitId)).thenReturn(Optional.of(benefit));
        when(employeeRepository.findAllActive()).thenReturn(List.of());

        // Set cho modelMapper phải trả về gì để tránh NullPointerException.
        // Đối tượng trả về cần có endDate để logic trong service tiếp tục chạy.
        Benefit mappedFromPatch = new Benefit();

        // Dùng Argument Matchers để đảm bảo stubbing luôn hoạt động
        when(modelMapper.map(any(PatchBenefitDTO.class), eq(Benefit.class))).thenReturn(mappedFromPatch);
        // ====================================================


        // Act & Assert
        HRMSAPIException exception = assertThrows(HRMSAPIException.class, () -> {
            benefitService.updateBenefit(patchDTO, benefitId);
        });

        assertEquals("Ngày kết thúc phải lớn hơn ngày bắt đầu", exception.getMessage());
        verify(benefitRepository, never()).save(any());
    }

    //@@@@@@Test deleteBenefit()@@@@@@@
    // ######SUCCESS CASE########
    @Test
    @DisplayName("Test deleteBenefit - Xóa phúc lợi thành công")
    void testDeleteBenefit_Success() {
        // Arrange
        Long benefitId = 1L;

        // Giả lập rằng repository tìm thấy phúc lợi
        when(benefitRepository.findById(benefitId)).thenReturn(Optional.of(benefit));

        // Giả lập rằng modelMapper sẽ chuyển đổi entity đã tìm thấy sang DTO
        when(modelMapper.map(benefit, BenefitDTO.class)).thenReturn(benefitDTO);

        // Phương thức deleteById trả về void, không cần mock thenReturn,
        // => dùng verify để kiểm tra nó có được gọi hay không.
        // doNothing().when(benefitRepository).deleteById(benefitId); // Dòng này không bắt buộc

        // Act
        BenefitDTO deletedDto = benefitService.deleteBenefit(benefitId);

        // Assert
        assertNotNull(deletedDto);
        assertEquals(benefit.getId(), deletedDto.getId());
        assertEquals(benefit.getTitle(), deletedDto.getTitle());

        // Verify
        // Kiểm tra xem phương thức findById có được gọi đúng 1 lần với ID chính xác không
        verify(benefitRepository, times(1)).findById(benefitId);

        // Quan trọng: Kiểm tra xem phương thức deleteById có được gọi đúng 1 lần với ID chính xác không
        verify(benefitRepository, times(1)).deleteById(benefitId);

        // Kiểm tra xem modelMapper có được gọi để chuyển đổi đối tượng không
        verify(modelMapper, times(1)).map(benefit, BenefitDTO.class);
    }

    //@@@@@@Test deleteBenefit()@@@@@@@
    // ######FAILED CASE########
    @Test
    @DisplayName("Test deleteBenefit - Ném ngoại lệ khi không tìm thấy Benefit")
    void testDeleteBenefit_ThrowsException_WhenBenefitNotFound() {
        // Arrange
        Long nonExistentId = 99L;

        // Giả lập rằng repository không tìm thấy phúc lợi nào với ID này
        when(benefitRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // Act & Assert
        HRMSAPIException exception = assertThrows(HRMSAPIException.class, () -> {
            benefitService.deleteBenefit(nonExistentId);
        });

        assertEquals("Benefit with id " + nonExistentId + " is not existed.", exception.getMessage());

        // Verify
        // Đảm bảo rằng phương thức deleteById không bao giờ được gọi
        verify(benefitRepository, never()).deleteById(anyLong());
        verify(modelMapper, never()).map(any(), any());
    }

    //@@@@@@Test getEmployeeAndPositionRegistrationByBenefitId()@@@@@@@
    // ######SUCCESS CASE########
    @Test
    @DisplayName("Test getByBenefitId - Lấy thông tin thành công khi tìm thấy Benefit")
    void testGetEmployeeAndPositionRegistrationByBenefitId_Success() {
        // Arrange
        Long benefitId = 1L;
        Integer pageNumber = 1;
        Integer pageSize = 10;

        // Tạo đối tượng Pageable mà service sẽ tạo ra để có thể so khớp trong mock
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize, Sort.by("id").ascending());

        // Vì tìm theo ID, kết quả trả về trong Page sẽ chỉ có 1 đối tượng
        // Sử dụng đối tượng 'benefit' và 'benefitDTO' từ @BeforeEach
        List<Benefit> benefitList = List.of(benefit);
        Page<Benefit> benefitPage = new PageImpl<>(benefitList, pageable, 1);

        List<BenefitDTO> dtoList = List.of(benefitDTO);

        // Định nghĩa hành vi của mock
        // Khi repository được gọi với bất kỳ Specification và Pageable nào, trả về trang đã tạo
        when(benefitRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(benefitPage);
        when(benefitMapper.toBenefitDTOs(benefitList)).thenReturn(dtoList);

        // Act
        BenefitResponse response = benefitService.getEmployeeAndPositionRegistrationByBenefitId(benefitId, pageNumber, pageSize, "id", "asc");

        // Assert
        assertNotNull(response);
        assertEquals(1, response.getTotalElements());
        assertEquals(1, response.getContent().size());
        assertEquals(benefitId, response.getContent().get(0).getId());
        assertEquals(0, response.getPageNumber()); // Page number của Spring Data JPA bắt đầu từ 0
        assertEquals(10, response.getPageSize());
        assertTrue(response.isLastPage());

        // Verify
        verify(benefitRepository, times(1)).findAll(any(Specification.class), any(Pageable.class));
        verify(benefitMapper, times(1)).toBenefitDTOs(benefitList);
    }


    //@@@@@@Test getEmployeeAndPositionRegistrationByBenefitId()@@@@@@@
    // ######FAILED CASE########
    @Test
    @DisplayName("Test getByBenefitId - Ném ngoại lệ khi không tìm thấy Benefit")
    void testGetEmployeeAndPositionRegistrationByBenefitId_ThrowsException_WhenBenefitNotFound() {
        // Arrange
        Long nonExistentId = 99L;

        // Giả lập repository trả về một trang rỗng
        Page<Benefit> emptyPage = Page.empty();
        when(benefitRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(emptyPage);

        // Act & Assert
        HRMSAPIException exception = assertThrows(HRMSAPIException.class, () -> {
            benefitService.getEmployeeAndPositionRegistrationByBenefitId(nonExistentId, 1, 10, "id", "asc");
        });

        assertEquals("Không có phúc lợi tương ứng với id " + nonExistentId, exception.getMessage());

        // Verify
        verify(benefitMapper, never()).toBenefitDTOs(any());
    }

    //@@@@@@Test getBenefitById()@@@@@@@
    // ######SUCCESS CASE########
    @Test
    @DisplayName("Test getBenefitById - Trả về DTO thành công khi tìm thấy")
    void testGetBenefitById_Success() {
        // Arrange
        Long benefitId = 1L;

        // Sử dụng các đối tượng đã có từ @BeforeEach
        // Giả lập repository tìm thấy 'benefit'
        when(benefitRepository.findById(benefitId)).thenReturn(Optional.of(benefit));

        // Giả lập modelMapper chuyển đổi 'benefit' (không phải Optional) sang 'benefitDTO'
        when(modelMapper.map(benefit, BenefitDTO.class)).thenReturn(benefitDTO);

        // Act
        BenefitDTO result = benefitService.getBenefitById(benefitId);

        // Assert
        assertNotNull(result);
        assertEquals(benefitId, result.getId());
        assertEquals(benefit.getTitle(), result.getTitle());

        // Verify
        verify(benefitRepository, times(1)).findById(benefitId);
        verify(modelMapper, times(1)).map(benefit, BenefitDTO.class);
    }


    //@@@@@@Test getBenefitById()@@@@@@@
    // ######FAILED CASE########
    @Test
    @DisplayName("Test getBenefitById - Ném ngoại lệ khi không tìm thấy")
    void testGetBenefitById_ThrowsException_WhenNotFound() {
        // Arrange
        Long nonExistentId = 99L;

        // Giả lập repository trả về một Optional rỗng
        when(benefitRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // Act & Assert
        HRMSAPIException exception = assertThrows(HRMSAPIException.class, () -> {
            benefitService.getBenefitById(nonExistentId);
        });

        assertEquals("Phúc lợi với id " + nonExistentId + " không tồn tại.", exception.getMessage());

        // Verify
        // Đảm bảo modelMapper không bao giờ được gọi khi không tìm thấy benefit
        verify(modelMapper, never()).map(any(), any());
    }

    //@@@@@@Test getEmployeeByPositionAndBenefit()@@@@@@@
    // ######SUCCESS CASE########
    @Test
    @DisplayName("Test getByPositionAndBenefit - Thành công khi chỉ có benefitId (positionId is null)")
    void testGetByPositionAndBenefit_Success_WhenPositionIdIsNull() {
        // Arrange
        Long benefitId = 1L;
        Long positionId = null; // Trường hợp không lọc theo position

        // Giả lập repository trả về trang chứa benefit với đầy đủ 2 positions
        Page<Benefit> benefitPage = new PageImpl<>(List.of(benefit));
        when(benefitRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(benefitPage);
        when(benefitMapper.toBenefitDTOs(anyList())).thenReturn(List.of(benefitDTO));

        // Dùng ArgumentCaptor để bắt đối tượng được truyền cho mapper
        ArgumentCaptor<List<Benefit>> benefitsCaptor = ArgumentCaptor.forClass(List.class);

        // Act
        BenefitResponse response = benefitService.getEmployeeByPositionAndBenefit(benefitId, positionId, 1, 10, "id", "asc");

        // Assert
        assertNotNull(response);

        // Verify và kiểm tra
        verify(benefitMapper).toBenefitDTOs(benefitsCaptor.capture());
        List<Benefit> capturedBenefits = benefitsCaptor.getValue();

        // Vì positionId là null, list BenefitPosition không bị lọc và vẫn giữ nguyên 2 phần tử
        assertEquals(1, capturedBenefits.size());
        assertEquals(2, capturedBenefits.get(0).getBenefitPositions().size(), "Danh sách vị trí không được lọc khi positionId là null");
    }

    @Test
    @DisplayName("Test getByPositionAndBenefit - Thành công và lọc đúng vị trí khi có cả benefitId và positionId")
    void testGetByPositionAndBenefit_Success_WithPositionIdProvided() {
        // Arrange
        Long benefitId = 1L;
        Long positionIdToFilter = 10L; // Chỉ lọc lấy Developer

        Page<Benefit> benefitPage = new PageImpl<>(List.of(benefit));
        when(benefitRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(benefitPage);
        when(benefitMapper.toBenefitDTOs(anyList())).thenReturn(List.of(benefitDTO));

        ArgumentCaptor<List<Benefit>> benefitsCaptor = ArgumentCaptor.forClass(List.class);

        // Act
        BenefitResponse response = benefitService.getEmployeeByPositionAndBenefit(benefitId, positionIdToFilter, 1, 10, "id", "asc");

        // Assert
        assertNotNull(response);

        // Verify và kiểm tra logic lọc in-memory
        verify(benefitMapper).toBenefitDTOs(benefitsCaptor.capture());
        List<Benefit> capturedBenefits = benefitsCaptor.getValue();
        Benefit capturedBenefit = capturedBenefits.get(0);

        // Danh sách BenefitPosition phải được lọc và chỉ còn 1 phần tử
        assertEquals(1, capturedBenefit.getBenefitPositions().size(), "Danh sách vị trí phải được lọc lại còn 1");
        // Vị trí còn lại phải là Developer (ID 10L)
        assertEquals(positionIdToFilter, capturedBenefit.getBenefitPositions().get(0).getPosition().getPositionId());
    }


    //@@@@@@Test getEmployeeByPositi    onAndBenefit()@@@@@@@
    // ######FAILED CASE########
    @Test
    @DisplayName("Test getByPositionAndBenefit - Ném ngoại lệ khi không tìm thấy Benefit theo benefitId")
    void testGetByPositionAndBenefit_ThrowsException_WhenBenefitNotFound() {
        // Arrange
        Long nonExistentBenefitId = 999L;
        Long positionId = null;

        // Giả lập repository trả về trang rỗng
        when(benefitRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(Page.empty());

        // Act & Assert
        HRMSAPIException exception = assertThrows(HRMSAPIException.class, () -> {
            benefitService.getEmployeeByPositionAndBenefit(nonExistentBenefitId, positionId, 1, 10, "id", "asc");
        });

        // Kiểm tra đúng thông báo lỗi khi positionId là null
        assertEquals("Không có phúc lợi nào với id " + nonExistentBenefitId + ".", exception.getMessage());
        verify(benefitMapper, never()).toBenefitDTOs(any());
    }

    @Test
    @DisplayName("Test getByPositionAndBenefit - Ném ngoại lệ khi không có vị trí nào khớp")
    void testGetByPositionAndBenefit_ThrowsException_WhenPositionNotFoundForBenefit() {
        // Arrange
        Long benefitId = 1L;
        Long nonExistentPositionId = 99L;

        // Giả lập repository trả về trang rỗng (vì JOIN không có kết quả)
        when(benefitRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(Page.empty());

        // Act & Assert
        HRMSAPIException exception = assertThrows(HRMSAPIException.class, () -> {
            benefitService.getEmployeeByPositionAndBenefit(benefitId, nonExistentPositionId, 1, 10, "id", "asc");
        });

        // Kiểm tra đúng thông báo lỗi khi có cả benefitId và positionId
        String expectedMessage = "Không có BenefitPostiotion nào được tìm thấy với benefit id " + benefitId + " và position id " + nonExistentPositionId + ".";
        assertEquals(expectedMessage, exception.getMessage());
        verify(benefitMapper, never()).toBenefitDTOs(any());
    }

    //@@@@@@Test getAllActive()@@@@@@@
    // ######SUCCESS CASE########
    @Test
    @DisplayName("Test getAllActive - Trả về danh sách các phúc lợi đang hoạt động")
    void testGetAllActive_ShouldReturnListOfActiveBenefits() {
        // Arrange (Sắp đặt)
        // Tạo một danh sách phúc lợi mẫu để repository trả về.
        // Chúng ta có thể dùng đối tượng 'benefit' đã có từ @BeforeEach.
        Benefit benefit2 = new Benefit();
        benefit2.setId(2L);
        benefit2.setTitle("Khám sức khỏe định kỳ 2025");
        benefit2.setIsActive(true);

        List<Benefit> activeBenefits = List.of(benefit, benefit2);

        // Dạy cho mock repository: khi phương thức findByIsActiveTrue được gọi,
        // hãy trả về danh sách activeBenefits đã tạo.
        when(benefitRepository.findByIsActiveTrue()).thenReturn(activeBenefits);

        // Act (Hành động)
        List<Benefit> result = benefitService.getAllActive();

        // Assert (Kiểm chứng)
        assertNotNull(result, "Kết quả trả về không được null");
        assertEquals(2, result.size(), "Kích thước danh sách trả về phải là 2");
        assertEquals("Du lịch công ty 2025", result.get(0).getTitle());

        // Verify (Xác thực)
        // Đảm bảo rằng phương thức findByIsActiveTrue của repository được gọi đúng 1 lần.
        verify(benefitRepository, times(1)).findByIsActiveTrue();
    }

    //@@@@@@Test getAllActive()@@@@@@@
    // ######FAILED CASE########
    @Test
    @DisplayName("Test getAllActive - Trả về danh sách rỗng khi không có phúc lợi nào hoạt động")
    void testGetAllActive_ShouldReturnEmptyList_WhenNoneFound() {
        // Arrange (Sắp đặt)
        // Dạy cho mock repository trả về một danh sách rỗng.
        when(benefitRepository.findByIsActiveTrue()).thenReturn(Collections.emptyList());

        // Act (Hành động)
        List<Benefit> result = benefitService.getAllActive();

        // Assert (Kiểm chứng)
        assertNotNull(result, "Kết quả trả về không được null, phải là một danh sách rỗng");
        assertTrue(result.isEmpty(), "Danh sách trả về phải rỗng");

        // Verify (Xác thực)
        // Đảm bảo rằng phương thức findByIsActiveTrue của repository vẫn được gọi 1 lần.
        verify(benefitRepository, times(1)).findByIsActiveTrue();
    }

    //@@@@@@Test getsearchBenefitByKeyword()@@@@@@@
    // ######SUCCESS CASE########
    @Test
    @DisplayName("Test searchBenefitByKeyword - Thành công khi tìm thấy kết quả")
    void testSearchBenefitByKeyword_Success_WhenBenefitsFound() {
        // Arrange
        String keyword = "Trip";
        Integer pageNumber = 1;
        Integer pageSize = 5;
        Pageable pageable = PageRequest.of(0, 5, Sort.by("title").ascending());

        // Tạo dữ liệu mẫu
        Benefit benefit1 = new Benefit();
        benefit1.setId(1L);
        benefit1.setTitle("Company Trip 2025");
        BenefitDTO dto1 = new BenefitDTO();
        dto1.setId(1L);
        dto1.setTitle("Company Trip 2025");

        List<Benefit> foundBenefits = List.of(benefit1);
        Page<Benefit> benefitPage = new PageImpl<>(foundBenefits, pageable, 1);

        // Dạy cho mock repository trả về trang dữ liệu đã tạo
        when(benefitRepository.findByTitleLikeIgnoreCase(anyString(), any(Pageable.class))).thenReturn(benefitPage);
        // Dạy cho mock modelMapper cách map từng đối tượng
        when(modelMapper.map(benefit1, BenefitDTO.class)).thenReturn(dto1);

        // Tạo ArgumentCaptor để bắt các tham số được truyền vào repository
        ArgumentCaptor<String> keywordCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);

        // Act
        BenefitResponse response = benefitService.searchBenefitByKeyword(keyword, pageNumber, pageSize, "title", "asc");

        // Assert
        assertNotNull(response);
        assertEquals(1, response.getTotalElements());
        assertEquals(1, response.getContent().size());
        assertEquals("Company Trip 2025", response.getContent().get(0).getTitle());
        assertEquals(0, response.getPageNumber());

        // Verify
        // Kiểm tra xem repository có được gọi với đúng các tham số không
        verify(benefitRepository).findByTitleLikeIgnoreCase(keywordCaptor.capture(), pageableCaptor.capture());

        // Kiểm tra keyword có được bao bọc bởi '%' hay không
        assertEquals("%" + keyword + "%", keywordCaptor.getValue());
        // Kiểm tra Pageable có đúng thông số không
        assertEquals(0, pageableCaptor.getValue().getPageNumber());
        assertEquals(5, pageableCaptor.getValue().getPageSize());
    }



}
