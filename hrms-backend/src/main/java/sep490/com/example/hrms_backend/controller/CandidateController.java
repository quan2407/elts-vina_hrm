package sep490.com.example.hrms_backend.controller;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import sep490.com.example.hrms_backend.dto.CandidateDto;
import sep490.com.example.hrms_backend.service.CandidateService;

@RestController
@RequestMapping(path = "/api/candidate", produces = (MediaType.APPLICATION_JSON_VALUE))
@AllArgsConstructor
@Validated
public class CandidateController {

    @Autowired
    private CandidateService candidateService;

    @PostMapping("/apply/{recruitmentId}")
    public ResponseEntity<?> applyCandidate(
            @RequestBody CandidateDto candidateDto,
            @PathVariable Long recruitmentId) {
        try {
            candidateService.saveOrUpdateCandidateByPhone(candidateDto, recruitmentId);
            return ResponseEntity.status(HttpStatus.CREATED).body("Ứng viên đã ứng tuyển thành công");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Lỗi: " + e.getMessage());
        }
    }

}
