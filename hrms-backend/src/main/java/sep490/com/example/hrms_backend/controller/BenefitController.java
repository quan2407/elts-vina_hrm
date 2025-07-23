package sep490.com.example.hrms_backend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import sep490.com.example.hrms_backend.config.AppConstants;
import sep490.com.example.hrms_backend.dto.PositionDTO;
import sep490.com.example.hrms_backend.dto.benefit.*;
import sep490.com.example.hrms_backend.entity.Benefit;
import sep490.com.example.hrms_backend.enums.BenefitType;
import sep490.com.example.hrms_backend.exception.HRMSAPIException;
import sep490.com.example.hrms_backend.security.CustomUserDetailsService;
import sep490.com.example.hrms_backend.service.BenefitPositionService;
import sep490.com.example.hrms_backend.service.BenefitService;
import sep490.com.example.hrms_backend.service.PositionService;
import sep490.com.example.hrms_backend.utils.CurrentUserUtils;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class BenefitController {

    @Autowired
    private BenefitService benefitService;

    @Autowired
    private PositionService positionService;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;


    @Autowired
    private BenefitPositionService benefitPositionService;


    private final CurrentUserUtils currentUserUtils;


    //1.View Benefit (Employee, HR)
    @PreAuthorize("hasAnyRole( 'HR')")
    @GetMapping("/hr/benefits")
    public ResponseEntity<BenefitResponse> getAllBenefitForHr(@RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false)Integer pageNumber,
                                                           @RequestParam(name = "pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
                                                           @RequestParam(name = "sortBy", defaultValue = AppConstants.SORT_BY, required = false) String sortBy,
                                                           @RequestParam(name = "sortOrder", defaultValue = AppConstants.SORT_DIRECTION, required = false) String sortOrder,
                                                         @RequestParam(name = "title", required = false) String title,
                                                         @RequestParam(name = "description", required = false) String description,
                                                         @RequestParam(name = "isActive", required = false) Boolean isActive,
                                                         @RequestParam(name = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                                         @RequestParam(name = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
                                                         @RequestParam(name = "minParticipants", required = false) Integer minParticipants,
                                                         @RequestParam(name = "maxParticipants", required = false) Integer maxParticipants,
                                                              @RequestParam(name="benefitType", required = false) BenefitType benefitType
    ){
        String username = currentUserUtils.getCurrentUsername();

        BenefitResponse benefitResponse = benefitService.getAllBenefitsForHr(username, pageNumber, pageSize, sortBy, sortOrder, title, description, isActive, startDate, endDate, minParticipants, maxParticipants, benefitType);
        return new ResponseEntity<>(benefitResponse, HttpStatus.OK  );
    }

  @PreAuthorize("hasAnyRole( 'HR')")
    @GetMapping("/hr/benefits/{benefitId}")
    public ResponseEntity<BenefitResponse> getEmployeeAndPositionRegistrationByBenefitId(@PathVariable Long benefitId,
                                                                                         @RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false)Integer pageNumber,
                                                                                         @RequestParam(name = "pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
                                                                                         @RequestParam(name = "sortBy", defaultValue = AppConstants.SORT_BY, required = false) String sortBy,
                                                                                         @RequestParam(name = "sortOrder", defaultValue = AppConstants.SORT_DIRECTION, required = false) String sortOrder
    ){

        BenefitResponse benefitResponse = benefitService.getEmployeeAndPositionRegistrationByBenefitId(benefitId,pageNumber, pageSize, sortBy, sortOrder);
        return new ResponseEntity<>(benefitResponse, HttpStatus.OK );
    }

    @GetMapping("/hr/benefit/{id}")
    public ResponseEntity<BenefitDTO> getBenefit(@PathVariable Long id) {
        BenefitDTO benefitDTO = benefitService.getBenefitById(id);
        return ResponseEntity.ok(benefitDTO);
    }

//get AvailablePosition
@PreAuthorize("hasRole('HR')")
@GetMapping("/hr/benefits/{benefitId}/available-positions")
public ResponseEntity<List<PositionDTO>> getAvailablePositions(
        @PathVariable Long benefitId) {
    List<PositionDTO> availablePositions = positionService.getPositionsNotRegisteredToBenefit(benefitId);
    return ResponseEntity.ok(availablePositions);
}

//assignPositionsToBenefit
@PostMapping("/hr/benefits/assign")
public ResponseEntity<?> assignPositionsToBenefit(@RequestBody BenefitPositionDTO request) {
    benefitPositionService.assignPositionsToBenefit(request);
    return ResponseEntity.ok("Assigned successfully");
}

//unassignPositionInBenefit
@DeleteMapping("/hr/benefits/unassign/benefit/{benefitId}/position/{positionId}")
public ResponseEntity<?> unassignPositionFromBenefit(@PathVariable Long benefitId, @PathVariable Long positionId) {
    benefitPositionService.unassignPositionFromBenefit(benefitId, positionId);
    return ResponseEntity.ok("Unassigned successfully.");
}
    //updateFormula
    @PutMapping("/hr/benefits/formula")
    public ResponseEntity<?> updateFormula(@RequestBody BenefitPositionUpdateDTO request) {
        benefitPositionService.updateFormula(request);
        return ResponseEntity.ok("Cập nhật công thức thành công");
    }

    //2. create Benefit (HR)
    @PreAuthorize("hasAnyRole('HR')")
    @PostMapping("/hr/benefits")
    public ResponseEntity<BenefitDTO> addBenefit(@Valid @RequestBody BenefitDTO benefitDTO , Authentication authentication){
        //####add info user created to admin can be checked log (not still pass into method)
        String username = currentUserUtils.getCurrentUsername();

        BenefitDTO createdBenefitDTO = benefitService.addBenefit(benefitDTO);
        return new ResponseEntity<>(createdBenefitDTO, HttpStatus.CREATED);
    }

//  3. update Benefit (HR)
    @PreAuthorize("hasAnyRole('HR')")
    @PatchMapping("/hr/benefits/{benefitId}")
    public ResponseEntity<BenefitDTO> updateBenefit(@Valid @RequestBody PatchBenefitDTO benefitDTO, @PathVariable Long benefitId){

        BenefitDTO benefitDTO1 = benefitService.updateBenefit(benefitDTO, benefitId);
        return new ResponseEntity<>(benefitDTO1, HttpStatus.OK);
    }


    //4. change the status Benefit (Active/InActive) (HR)
    @PreAuthorize("hasAnyRole('HR')")
    @PatchMapping("/{benefitId}")
    public ResponseEntity<BenefitDTO> updateInactiveStatus(
            @PathVariable Long benefitId,
            @RequestBody BenefitStatusUpdateRequestDTO benefitStatusUpdateRequestDTO
    ) {

        BenefitDTO updated = benefitService.updateInactiveStatus(benefitId, benefitStatusUpdateRequestDTO.isActive());
        return ResponseEntity.ok(updated);
    }

    //5. get benefits by keyword
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

    //6. delete benefit
    @DeleteMapping("/hr/benefits/{benefitId}")
    public ResponseEntity<BenefitDTO> deleteBenefit(@PathVariable Long benefitId){
        BenefitDTO benefitDTO = benefitService.deleteBenefit(benefitId);
        return new ResponseEntity<>(benefitDTO, HttpStatus.OK);
    }


    


}
