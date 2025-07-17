package sep490.com.example.hrms_backend.service.impl;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sep490.com.example.hrms_backend.dto.benefit.BenefitDTO;
import sep490.com.example.hrms_backend.dto.benefit.BenefitRegistrationDTO;
import sep490.com.example.hrms_backend.dto.benefit.BenefitRegistrationResponse;
import sep490.com.example.hrms_backend.dto.benefit.BenefitResponse;
import sep490.com.example.hrms_backend.entity.Benefit;
import sep490.com.example.hrms_backend.entity.BenefitRegistration;
import sep490.com.example.hrms_backend.entity.Employee;
import sep490.com.example.hrms_backend.exception.HRMSAPIException;
import sep490.com.example.hrms_backend.repository.BenefitRegistrationRepository;
import sep490.com.example.hrms_backend.repository.BenefitRepository;
import sep490.com.example.hrms_backend.repository.EmployeeRepository;
import sep490.com.example.hrms_backend.service.BenefitRegistrationService;
import sep490.com.example.hrms_backend.service.EmployeeService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BenefitRegistrationImpl implements BenefitRegistrationService {


    private final BenefitRegistrationRepository benefitRegistrationRepository;

    private final ModelMapper modelMapper;

    private final EmployeeRepository employeeRepository;

    private final BenefitRepository benefitRepository;

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
}
