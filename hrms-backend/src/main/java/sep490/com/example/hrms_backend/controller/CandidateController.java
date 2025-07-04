package sep490.com.example.hrms_backend.controller;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import sep490.com.example.hrms_backend.dto.CandidateDto;
import sep490.com.example.hrms_backend.dto.CandidateResponseDTO;
import sep490.com.example.hrms_backend.service.CandidateService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path = "/api/candidate", produces = (MediaType.APPLICATION_JSON_VALUE))
@AllArgsConstructor
@Validated
public class CandidateController {

    @Autowired
    private CandidateService candidateService;

    @PostMapping("/apply/{recruitmentId}")
    public ResponseEntity<?> applyCandidate(
            @Valid @RequestBody CandidateDto candidateDto,
            @PathVariable Long recruitmentId) {
        try {
            candidateService.saveOrUpdateCandidateByPhone(candidateDto, recruitmentId);
            return ResponseEntity.status(HttpStatus.CREATED).body("Ứng viên đã ứng tuyển thành công");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Lỗi: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('HR')")
    public ResponseEntity<?> getCandidateByRecruitment(@PathVariable Long id) {
        List<CandidateResponseDTO> candidates = candidateService.getCandidatesByRecruitmentId(id);
        return ResponseEntity.ok(candidates);
    }

//    @PutMapping("/{id}/status")
//    public ResponseEntity<?> updateStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
//        String status = body.get("status");
//        try {
//            candidateService.updateCandidateStatus(id, status);
//            return ResponseEntity.ok().build();
//        } catch (IllegalArgumentException e) {
//            return ResponseEntity.badRequest().body("Trạng thái không hợp lệ.");
//        } catch (EntityNotFoundException e) {
//            return ResponseEntity.notFound().build();
//        }
//    }

}
