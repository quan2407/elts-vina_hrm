package sep490.com.example.hrms_backend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import sep490.com.example.hrms_backend.dto.InterviewScheduleDTO;
import sep490.com.example.hrms_backend.entity.CandidateRecruitment;
import sep490.com.example.hrms_backend.service.CandidateRecruitmentService;
import sep490.com.example.hrms_backend.service.InterviewScheduleService;

import java.util.List;

@RestController
@RequestMapping("/api/interview")
@RequiredArgsConstructor
public class InterviewScheduleController {

    @Autowired
    InterviewScheduleService interviewScheduleService;

    @GetMapping("/candidate-recruitment/{candidateRecruitmentId}")
    @PreAuthorize("hasAnyRole('HR')")

    public ResponseEntity<?> getInterviewInitData(@Valid @PathVariable Long candidateRecruitmentId) {
        InterviewScheduleDTO interviewScheduleDTO = interviewScheduleService.getInterviewByCandidateRecruitmentId(candidateRecruitmentId);
        return ResponseEntity.ok(interviewScheduleDTO);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('HR')")

    public ResponseEntity<?> getInterviewById(@Valid @PathVariable Long id) {
        InterviewScheduleDTO interviewScheduleDTO = interviewScheduleService.getInterviewById(id);
        return ResponseEntity.ok(interviewScheduleDTO);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('HR')")

    public ResponseEntity<?> getAllInterviewSchedule() {
        List<InterviewScheduleDTO> interviewScheduleDTOList = interviewScheduleService.getAllInterviewSchedule();
        if (interviewScheduleDTOList != null && !interviewScheduleDTOList.isEmpty()) {
            return new ResponseEntity<>(interviewScheduleDTOList, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('HR')")
    public ResponseEntity<?> createInterviewSchedule(@Valid @RequestBody InterviewScheduleDTO dto) {
        InterviewScheduleDTO created = interviewScheduleService.createInterviewSchedule(dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('HR')")
    public ResponseEntity<?> editInterview(@PathVariable Long id, @Valid @RequestBody InterviewScheduleDTO interviewScheduleDTO) {
        try {
            InterviewScheduleDTO updateDto = interviewScheduleService.editInterview(id, interviewScheduleDTO);
            return new ResponseEntity<>(updateDto, HttpStatus.OK);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}