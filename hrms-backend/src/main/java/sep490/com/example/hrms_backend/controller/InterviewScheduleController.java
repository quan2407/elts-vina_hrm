package sep490.com.example.hrms_backend.controller;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sep490.com.example.hrms_backend.dto.InterviewScheduleDTO;
import sep490.com.example.hrms_backend.service.InterviewScheduleService;
import sep490.com.example.hrms_backend.utils.CurrentUserUtils;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/interview")
@RequiredArgsConstructor
public class InterviewScheduleController {

    @Autowired
    InterviewScheduleService interviewScheduleService;
    private final CurrentUserUtils currentUserUtils;

    @GetMapping("/candidate-recruitment/{candidateRecruitmentId}")
    public ResponseEntity<?> getInterviewInitData(@Valid @PathVariable Long candidateRecruitmentId) {
        InterviewScheduleDTO interviewScheduleDTO = interviewScheduleService.getInterviewByCandidateRecruitmentId(candidateRecruitmentId);
        return ResponseEntity.ok(interviewScheduleDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getInterviewById(@Valid @PathVariable Long id) {
        InterviewScheduleDTO interviewScheduleDTO = interviewScheduleService.getInterviewById(id);
        return ResponseEntity.ok(interviewScheduleDTO);
    }

    @GetMapping
    public ResponseEntity<?> getAllInterviewSchedule() {
        List<InterviewScheduleDTO> interviewScheduleDTOList = interviewScheduleService.getAllInterviewSchedule();
        if (interviewScheduleDTOList != null && !interviewScheduleDTOList.isEmpty()) {
            return new ResponseEntity<>(interviewScheduleDTOList, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping
    public ResponseEntity<?> createInterviewSchedule(@Valid @RequestBody InterviewScheduleDTO dto) {
        Long employeeId = currentUserUtils.getCurrentEmployeeId();
        InterviewScheduleDTO created = interviewScheduleService.createInterviewSchedule(dto, employeeId);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> editInterview(@PathVariable Long id, @Valid @RequestBody InterviewScheduleDTO interviewScheduleDTO) {
        try {
            InterviewScheduleDTO updateDto = interviewScheduleService.editInterview(id, interviewScheduleDTO);
            return new ResponseEntity<>(updateDto, HttpStatus.OK);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String status = body.get("status");
        try {
            interviewScheduleService.updateStatus(id, status);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Trạng thái không hợp lệ.");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}/result")
    public ResponseEntity<?> updateResult(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String result = body.get("result");
        try {
            interviewScheduleService.updateResult(id, result);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Kết quả không hợp lệ.");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

}