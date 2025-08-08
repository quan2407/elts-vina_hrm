package sep490.com.example.hrms_backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import sep490.com.example.hrms_backend.dto.benefit.BenefitManualRegistrationRequest;
import sep490.com.example.hrms_backend.dto.benefit.EmployeeBasicDetailResponse;
import sep490.com.example.hrms_backend.service.BenefitRegistrationService;
import sep490.com.example.hrms_backend.service.BenefitService;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class BenefitRegistrationController {


    private final BenefitRegistrationService benefitRegistrationService;
    private final BenefitService benefitService;

    //1. get benefit by employee
//    @GetMapping("/employees/{employeeId}")
//    public ResponseEntity<BenefitResponse> getBenefitByEmployee(@PathVariable Long employeeId, @RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false)Integer pageNumber,
//                                                                @RequestParam(name = "pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
//                                                                @RequestParam(name = "sortBy", defaultValue = AppConstants.SORT_BY, required = false) String sortBy,
//                                                                @RequestParam(name = "sortOrder", defaultValue = AppConstants.SORT_DIRECTION, required = false) String sortOrder){
//
//        BenefitResponse benefitResponse = benefitRegistrationService.searchBenefitByEmployee(employeeId, pageNumber, pageSize, sortBy, sortOrder);
//        return new ResponseEntity<>(benefitResponse, HttpStatus.FOUND );
//    }




    //2. đăng kí Benefit
//    @PostMapping("register/employees/{employeeId}/benefits/{benefitId}")
//    public ResponseEntity<BenefitRegistrationDTO> registerBenefit(@PathVariable Long employeeId, @PathVariable Long benefitId, @RequestBody(required = false) String note){
//        BenefitRegistrationDTO benefitRegistrationDTO = benefitRegistrationService.registerBenefitForEmployee(benefitId, employeeId, note);
//        return new ResponseEntity<>(benefitRegistrationDTO, HttpStatus.CREATED);
//    }

    @PreAuthorize("hasAnyRole( 'HR')")
    @PostMapping("/hr/benefits/quick-register")
    public ResponseEntity<?> quickRegister(@RequestBody BenefitManualRegistrationRequest request) {
        try {
            benefitRegistrationService.quickRegister(request);
            return ResponseEntity.ok("Đăng ký thành công");
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    @PreAuthorize("hasAnyRole( 'HR')")
    @DeleteMapping("/hr/benefits/un-register/benefit/{benefitId}/position/{positionId}/employee/{employeeId}")
    public ResponseEntity<?> unRegister(@PathVariable Long benefitId, @PathVariable Long positionId,@PathVariable Long employeeId) {
        benefitRegistrationService.unRegister(benefitId, positionId, employeeId);
        return ResponseEntity.ok("Un-register successfully");
    }

    @PreAuthorize("hasAnyRole( 'HR')")
    @GetMapping("/hr/benefits/search-unregistered")
    public ResponseEntity<List<EmployeeBasicDetailResponse>> searchUnregisteredEmployees(
            @RequestParam Long benefitId,
            @RequestParam Long positionId,
            @RequestParam(required = false) String keyword) {

        List<EmployeeBasicDetailResponse> result = benefitRegistrationService
                .searchUnregisteredEmployees(benefitId, positionId, keyword);

        return ResponseEntity.ok(result);
    }

}
