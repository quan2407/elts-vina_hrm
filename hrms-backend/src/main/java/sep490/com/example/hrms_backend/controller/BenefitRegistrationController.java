package sep490.com.example.hrms_backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sep490.com.example.hrms_backend.config.AppConstants;
import sep490.com.example.hrms_backend.dto.benefit.BenefitRegistrationDTO;
import sep490.com.example.hrms_backend.dto.benefit.BenefitRegistrationResponse;
import sep490.com.example.hrms_backend.dto.benefit.BenefitResponse;
import sep490.com.example.hrms_backend.service.BenefitRegistrationService;
import sep490.com.example.hrms_backend.service.BenefitService;

@RestController
@RequestMapping("api/benefit")
@RequiredArgsConstructor
public class BenefitRegistrationController {


    private final BenefitRegistrationService benefitRegistrationService;
    private final BenefitService benefitService;

    //1. get benefit by employee
    @GetMapping("/employees/{employeeId}")
    public ResponseEntity<BenefitResponse> getBenefitByEmployee(@PathVariable Long employeeId, @RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false)Integer pageNumber,
                                                                @RequestParam(name = "pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
                                                                @RequestParam(name = "sortBy", defaultValue = AppConstants.SORT_BY, required = false) String sortBy,
                                                                @RequestParam(name = "sortOrder", defaultValue = AppConstants.SORT_DIRECTION, required = false) String sortOrder){

        BenefitResponse benefitResponse = benefitRegistrationService.searchBenefitByEmployee(employeeId, pageNumber, pageSize, sortBy, sortOrder);
        return new ResponseEntity<>(benefitResponse, HttpStatus.FOUND );
    }


    //2. đăng kí Benefit
    @PostMapping("register/employees/{employeeId}/benefits/{benefitId}")
    public ResponseEntity<BenefitRegistrationDTO> registerBenefit(@PathVariable Long employeeId, @PathVariable Long benefitId, @RequestBody(required = false) String note){
        BenefitRegistrationDTO benefitRegistrationDTO = benefitRegistrationService.registerBenefitForEmployee(benefitId, employeeId, note);
        return new ResponseEntity<>(benefitRegistrationDTO, HttpStatus.CREATED);
    }

}
