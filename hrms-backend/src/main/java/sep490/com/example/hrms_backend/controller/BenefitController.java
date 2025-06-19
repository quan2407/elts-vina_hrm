package sep490.com.example.hrms_backend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import sep490.com.example.hrms_backend.config.AppConstants;
import sep490.com.example.hrms_backend.dto.benefit.BenefitDTO;
import sep490.com.example.hrms_backend.dto.benefit.BenefitResponse;
import sep490.com.example.hrms_backend.dto.benefit.BenefitStatusUpdateRequestDTO;
import sep490.com.example.hrms_backend.exception.HRMSAPIException;
import sep490.com.example.hrms_backend.security.CustomUserDetailsService;
import sep490.com.example.hrms_backend.service.BenefitService;
import sep490.com.example.hrms_backend.utils.CurrentUserUtils;

@RestController
@RequestMapping("/api/benefit")
@RequiredArgsConstructor
public class BenefitController {

    @Autowired
    private BenefitService benefitService;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    private final CurrentUserUtils currentUserUtils;


    //View Benefit (Employee, HR)
//    @PreAuthorize("hasAnyRole('EMPLOYEE', 'HR')")
    @GetMapping
    public ResponseEntity<BenefitResponse> getAllBenefit(@RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false)Integer pageNumber,
                                                           @RequestParam(name = "pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
                                                           @RequestParam(name = "sortBy", defaultValue = AppConstants.SORT_BY, required = false) String sortBy,
                                                           @RequestParam(name = "sortOrder", defaultValue = AppConstants.SORT_DIRECTION, required = false) String sortOrder

    ){
        String username = currentUserUtils.getCurrentUsername();

        BenefitResponse benefitResponse = benefitService.getAllBenefits(username, pageNumber, pageSize, sortBy, sortOrder);
        return new ResponseEntity<>(benefitResponse, HttpStatus.OK  );
    }




    //create Benefit (HR)
    @PreAuthorize("hasAnyRole('HR')")
    @PostMapping
    public ResponseEntity<BenefitDTO> addBenefit(@Valid @RequestBody BenefitDTO benefitDTO , Authentication authentication){
        //####add info user created to admin can be checked log (not still pass into method)
        String username = currentUserUtils.getCurrentUsername();

        BenefitDTO createdBenefitDTO = benefitService.addBenefit(benefitDTO);
        return new ResponseEntity<>(createdBenefitDTO, HttpStatus.CREATED);
    }

//    //update Benefit (HR)
    @PreAuthorize("hasAnyRole('HR')")
    @PutMapping("/{benefitId}")
    public ResponseEntity<BenefitDTO> updateBenefit(@Valid @RequestBody BenefitDTO benefitDTO, @PathVariable Long benefitId){

        BenefitDTO benefitDTO1 = benefitService.updateBenefit(benefitDTO, benefitId);
        return new ResponseEntity<>(benefitDTO1, HttpStatus.OK);
    }


    //change the status Benefit (Active/InActive) (HR)
    @PreAuthorize("hasAnyRole('HR')")
    @PatchMapping("/{id}/inactive")
    public ResponseEntity<BenefitDTO> updateInactiveStatus(
            @PathVariable Long id,
            @RequestBody BenefitStatusUpdateRequestDTO benefitStatusUpdateRequestDTO
    ) {

        BenefitDTO updated = benefitService.updateInactiveStatus(id, benefitStatusUpdateRequestDTO.isActive());
        return ResponseEntity.ok(updated);
    }

    //get benefits by keyword
    @GetMapping ("/keyword/{keyword}")
    public ResponseEntity<BenefitResponse> getBenefitsByKeyword(@PathVariable String keyword,
                                                                @RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false)Integer pageNumber,
                                                                @RequestParam(name = "pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
                                                                @RequestParam(name = "sortBy", defaultValue = AppConstants.SORT_BY, required = false) String sortBy,
                                                                @RequestParam(name = "sortOrder", defaultValue = AppConstants.SORT_DIRECTION, required = false) String sortOrder){
                                            String username = currentUserUtils.getCurrentUsername();
                                            BenefitResponse benefitResponse = benefitService.searchBenefitByKeyword(keyword, pageNumber, pageSize, sortBy, sortOrder);
                                            return new ResponseEntity<>(benefitResponse, HttpStatus.FOUND );
                                }

    //get benefits by employee
    @GetMapping("/employees/{employeeId}")
    public ResponseEntity<BenefitResponse> getBenefitByEmployee(@PathVariable Long employeeId,@RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false)Integer pageNumber,
                                                                  @RequestParam(name = "pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
                                                                  @RequestParam(name = "sortBy", defaultValue = AppConstants.SORT_BY, required = false) String sortBy,
                                                                  @RequestParam(name = "sortOrder", defaultValue = AppConstants.SORT_DIRECTION, required = false) String sortOrder){

        BenefitResponse benefitResponse = benefitService.searchBenefitByEmployee(employeeId, pageNumber, pageSize, sortBy, sortOrder);
        return new ResponseEntity<>(benefitResponse, HttpStatus.FOUND );
    }

    





}
