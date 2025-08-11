package sep490.com.example.hrms_backend.service.impl;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sep490.com.example.hrms_backend.dto.benefit.*;
import sep490.com.example.hrms_backend.entity.*;
import sep490.com.example.hrms_backend.enums.FormulaType;
import sep490.com.example.hrms_backend.exception.HRMSAPIException;
import sep490.com.example.hrms_backend.repository.BenefitPositionRepository;
import sep490.com.example.hrms_backend.repository.BenefitRegistrationRepository;
import sep490.com.example.hrms_backend.repository.BenefitRepository;
import sep490.com.example.hrms_backend.repository.EmployeeRepository;
import sep490.com.example.hrms_backend.service.BenefitRegistrationService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BenefitRegistrationImpl implements BenefitRegistrationService {


    private final BenefitRegistrationRepository benefitRegistrationRepository;

    private final ModelMapper modelMapper;

    private final EmployeeRepository employeeRepository;

    private final BenefitRepository benefitRepository;

    private final BenefitPositionRepository benefitPositionRepository;


    //Tested
    @Transactional
    @Override
    public BenefitResponse searchBenefitByEmployee(Long employeeId, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        //find employee by Employeeid (exception)
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + employeeId));

        //Khoi tao sort
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                ?Sort.by(sortBy).ascending()
                :Sort.by(sortBy).descending();

        //Khoi tao pageable
        Pageable pageable = PageRequest.of(pageNumber  - 1 , pageSize, sortByAndOrder);

        //lay thong tin theo kieu page
        Page<Benefit> benefitPage = benefitRegistrationRepository.findByEmployeeOrderById(employee, pageable);

        //getContent trong Page (exeption)
        List<Benefit> benefits = benefitPage.getContent();

        //List -> List DTO
        List<BenefitDTO> benefitsDTO = benefits.stream().map(benefit -> modelMapper.map(benefit, BenefitDTO.class)).toList();

        //tao doi tuong benefitResponse và return
        BenefitResponse benefitResponse = new BenefitResponse();
        benefitResponse.setContent(benefitsDTO);
        benefitResponse.setPageNumber(benefitPage.getNumber());
        benefitResponse.setPageSize(benefitPage.getSize());
        benefitResponse.setTotalElements(benefitPage.getTotalElements());
        benefitResponse.setTotalPages(benefitPage.getTotalPages());
        benefitResponse.setLastPage(benefitPage.isLast());

        return benefitResponse;
    }

    //Tested
    @Override
    public BenefitRegistrationDTO registerBenefitForEmployee(Long benefitId, Long employeeId, String note) {
//        //Tim kiem thong tin nhan vien (exception: 0 co nhan vien)
//        Employee employee = employeeRepository.findById(employeeId)
//                .orElseThrow(() -> new HRMSAPIException("Employee not found with id: " + employeeId));
//
//        //Tim kiem phuc loi                        (exception:0 co phuc loi)
//        Benefit benefit = benefitRepository.findById(benefitId)
//                .orElseThrow(() -> new HRMSAPIException("Benefit not found with id: " + benefitId));
//
//        //Kiem tra trang thai phuc loi             exception: trang thai benefit dang inactive)
//        if(!benefit.getIsActive() || benefit.getEndDate().isBefore(java.time.LocalDate.now()) ) {
//            throw new HRMSAPIException("Benefit is not active or expired");
//        }
//
//        //Kiem tra so luong nguoi dang ki toi da (exception: so lg ng dky dat toi da)
//        long currentRegistrations = benefitRegistrationRepository.countByBenefit(benefit);
//        if(currentRegistrations >= benefit.getMaxParticipants()){
//            throw new HRMSAPIException("Maximum number of participants has been reached. Please contact the administrator for more information.");
//        }
//
//        //kiem tra xem nhan vien da dang ki chua (exception: nhan vien dang ki r)
//        boolean alreadyRegistered = benefitRegistrationRepository.existsByBenefitAndEmployee(benefit, employee);
//        if(alreadyRegistered){
//            throw new HRMSAPIException("You have already registered for this benefit. Please contact the administrator for more information.");
//        }
//
//        //tao ban dang ki moi
//        BenefitRegistration benefitRegistration = new BenefitRegistration();
//        BenefitRegistration newBenefitRegistration = BenefitRegistration.builder()
//                .benefit(benefit)
//                .employee(employee)
//                .registeredAt(LocalDateTime.now())
//                .isRegister(true)
//                .note(note)
//                .build();
//
//        return modelMapper.map(newBenefitRegistration, BenefitRegistrationDTO.class);
        return null;
    }

    //Tested
    @Override
    public void quickRegister(BenefitManualRegistrationRequest request) {
        List<String> keywords = request.getKeywords();
        List<String> failed = new ArrayList<>();


        // 1. Tìm BenefitPosition
        BenefitPosition benefitPosition = benefitPositionRepository
                .findByBenefit_IdAndPosition_PositionId(request.getBenefitId(), request.getPositionId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy BenefitPosition phù hợp."));

        Benefit benefit = benefitPosition.getBenefit();
        String benefitType = benefit.getBenefitType().name();
        FormulaType formulaType = benefitPosition.getFormulaType();
        BigDecimal formulaValue = benefitPosition.getFormulaValue();

        // 2. Lặp qua từng keyword
        for (String keyword : keywords) {
            Optional<Employee> employeeOpt = employeeRepository
                    .findByEmployeeNameIgnoreCaseOrEmailIgnoreCase(keyword, keyword);

            if (employeeOpt.isEmpty()) {
                failed.add(keyword + " (không tìm thấy nhân viên)");
                continue;
            }

            Employee employee = employeeOpt.get();
            Department employeeDepartment = employee.getDepartment();
            boolean isInPosition = employeeDepartment.getPositions()
                    .stream()
                    .anyMatch(p -> p.getPositionId().equals(request.getPositionId()));

            if (!isInPosition) {
                failed.add(keyword + " (không thuộc phòng ban có vị trí này)");
                continue;
            }

            // 3. Kiểm tra trùng đăng ký
            //update to commit
            if (benefitRegistrationRepository.existsByBenefitPositionAndEmployee(benefitPosition, employee)) {
                failed.add(keyword + " (đã đăng ký)");
                continue;
            }

//            // 6. Tính toán thay đổi basic_salary nếu cần
//            BigDecimal currentSalary = employee.getBasicSalary() != null ? employee.getBasicSalary() : BigDecimal.ZERO;
//
//            if ("PHU_CAP".equals(benefitType)) {
//                if (formulaType == FormulaType.AMOUNT) {
//                    currentSalary = currentSalary.add(formulaValue);
//                } else if (formulaType == FormulaType.PERCENTAGE) {
//                    currentSalary = currentSalary.add(currentSalary.multiply(formulaValue).divide(BigDecimal.valueOf(100)));
//                }
//            } else if ("KHAU_TRU".equals(benefitType)) {
//                if (formulaType == FormulaType.AMOUNT) {
//                    currentSalary = currentSalary.subtract(formulaValue);
//                } else if (formulaType == FormulaType.PERCENTAGE) {
//                    currentSalary = currentSalary.subtract(currentSalary.multiply(formulaValue).divide(BigDecimal.valueOf(100)));
//                }
//            }
            // SU_KIEN: Không thay đổi
//            employee.setBasicSalary(currentSalary);

            // 4. Đăng ký
            BenefitRegistration registration = new BenefitRegistration();
            registration.setBenefitPosition(benefitPosition);
            registration.setEmployee(employee);
            registration.setIsRegister(true);
            registration.setRegisteredAt(LocalDateTime.now());

            benefitRegistrationRepository.save(registration);
        }

        // 5. Nếu có lỗi, ném exception hoặc ghi log
        if (!failed.isEmpty()) {
            throw new RuntimeException("Một số nhân viên không thể đăng ký: " + String.join(", ", failed));
        }
}

    //Tested
    @Override
    public List<EmployeeBasicDetailResponse> searchUnregisteredEmployees(Long benefitId, Long positionId, String keyword) {
        // 1. Tìm BenefitPosition theo benefitId và positionId
        BenefitPosition benefitPosition = benefitPositionRepository
                .findByBenefit_IdAndPosition_PositionId(benefitId, positionId)
                .orElseThrow(() -> new HRMSAPIException("Không tìm thấy benefitPosition"));

        Long benefitPositionId = benefitPosition.getId();


        // 2. Tìm tất cả employee thuộc positionId (kèm keyword nếu có)
        List<Employee> employees;
        if (keyword != null && !keyword.trim().isEmpty()) {
            String likeKeyword = "%" + keyword.toLowerCase() + "%";
//            employees = employeeRepository
//                    .searchByPositionAndKeyword(positionId, keyword.trim().toLowerCase());
            employees = employeeRepository.searchByPositionAndKeyword(positionId, likeKeyword);
        } else {
            employees = employeeRepository.findByPosition_PositionId(positionId);
        }

        // 3. Lấy danh sách employeeId đã đăng ký rồi (với isRegister = true)
        List<Long> registeredEmployeeIds = benefitRegistrationRepository
                .findRegisteredEmployeeIdsByBenefitPositionId(benefitPositionId);


        // 4. Trả về danh sách chưa đăng ký
        return employees.stream()
                .filter(emp -> !registeredEmployeeIds.contains(emp.getEmployeeId()))
                .map(emp -> modelMapper.map(emp, EmployeeBasicDetailResponse.class))
                .collect(Collectors.toList());

    }

    //Tested
    @Transactional
    @Override
    public void unRegister(Long benefitId, Long positionId, Long employeeId) {
        BenefitPosition benefitPosition = benefitPositionRepository
                .findByBenefit_IdAndPosition_PositionId(benefitId, positionId)
                .orElseThrow(() -> new HRMSAPIException("Không tìm thấy benefitPosition"));

        Long benefitPositionId = benefitPosition.getId();
        benefitRegistrationRepository.deleteByBenefitPositionIdAndEmployeeId(benefitPositionId, employeeId);


    }

    //Tested
    @Transactional
    @Override
    public void quickRegisterAll(BenefitMultiPositionRequestDTO request) {
        Long benefitId = request.getBenefitId();
        List<Long> positionIds = request.getPositionIds();

        // 1. Lấy danh sách BenefitPosition tương ứng
        List<BenefitPosition> benefitPositions = benefitPositionRepository
                .findAllByBenefit_IdAndPosition_PositionIdIn(benefitId, positionIds);
        System.out.println(benefitPositions);

        if (benefitPositions.isEmpty()) {
            throw new RuntimeException("Không tìm thấy BenefitPosition phù hợp với benefitId và positionIds.");
        }

        boolean hasEmployees = false;

        // 2. Duyệt qua từng BenefitPosition
        for (BenefitPosition bp : benefitPositions) {
            Long positionId = bp.getPosition().getPositionId();

            // 3. Tìm nhân viên thuộc phòng ban có chứa position này
            List<Employee> employees = employeeRepository
                    .findAllByDepartment_Positions_PositionId(positionId);

            if (!employees.isEmpty()) {
                hasEmployees = true;
            }

            for (Employee employee : employees) {
                if (benefitRegistrationRepository.existsByBenefitPositionAndEmployee(bp, employee)) {
                    continue; // Skip nếu đã đăng ký
                }

                BenefitRegistration registration = new BenefitRegistration();
                registration.setBenefitPosition(bp);
                registration.setEmployee(employee);
                registration.setIsRegister(true);
                registration.setRegisteredAt(LocalDateTime.now());

                benefitRegistrationRepository.save(registration);
            }
        }

        if (!hasEmployees) {
            throw new RuntimeException("Không tìm thấy nhân viên nào cho các position.");
        }
    }
}
