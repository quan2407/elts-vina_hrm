package sep490.com.example.hrms_backend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sep490.com.example.hrms_backend.dto.benefit.*;
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

    @PostMapping("/hr/benefits/quick-register")
    public ResponseEntity<?> quickRegister(@RequestBody BenefitManualRegistrationRequest request) {
        try {
            benefitRegistrationService.quickRegister(request);
            return ResponseEntity.ok("Đăng ký thành công");
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    @DeleteMapping("/hr/benefits/un-register/benefit/{benefitId}/position/{positionId}/employee/{employeeId}")
    public ResponseEntity<?> unRegister(@PathVariable Long benefitId, @PathVariable Long positionId,@PathVariable Long employeeId) {
        benefitRegistrationService.unRegister(benefitId, positionId, employeeId);
        return ResponseEntity.ok("Un-register successfully");
    }

    @DeleteMapping("/hr/benefits/multi-un-register/benefit/{benefitId}/position/{positionId}")
    public ResponseEntity<UnregisterManyResponse> unRegisterMany(
            @PathVariable Long benefitId,
            @PathVariable Long positionId,
            @RequestBody @Valid UnregisterManyRequest request
    ) {
        int deleted = benefitRegistrationService.unRegisterMany(benefitId, positionId, request.getEmployeeIds());
        UnregisterManyResponse resp = new UnregisterManyResponse(
                request.getEmployeeIds() != null ? request.getEmployeeIds().size() : 0,
                deleted
        );
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/hr/benefits/search-unregistered")
    public ResponseEntity<List<EmployeeBasicDetailResponse>> searchUnregisteredEmployees(
            @RequestParam Long benefitId,
            @RequestParam Long positionId,
            @RequestParam(required = false) String keyword) {

        List<EmployeeBasicDetailResponse> result = benefitRegistrationService
                .searchUnregisteredEmployees(benefitId, positionId, keyword);

        return ResponseEntity.ok(result);
    }


    @PostMapping("/hr/benefits/quick-register-all")
    public ResponseEntity<?> quickRegisterAll(@RequestBody BenefitMultiPositionRequestDTO request) {
        try {
            benefitRegistrationService.quickRegisterAll(request);
            return ResponseEntity.ok("Đăng ký thành công");
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    @GetMapping("/hr/benefit/{benefitId}/position/{positionId}/stats")
    public ResponseEntity<PositionRegistrationStatsDTO> getStats(
            @PathVariable Long benefitId,
            @PathVariable Long positionId
    ) {
        PositionRegistrationStatsDTO stats = benefitRegistrationService.getRegistrationStats(benefitId, positionId);
        return ResponseEntity.ok(stats);
    }
    
}
