package sep490.com.example.hrms_backend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sep490.com.example.hrms_backend.dto.InterviewScheduleDTO;
import sep490.com.example.hrms_backend.entity.CandidateRecruitment;
import sep490.com.example.hrms_backend.service.CandidateRecruitmentService;
import sep490.com.example.hrms_backend.service.InterviewScheduleService;

@RestController
@RequestMapping("/api/interview")
@RequiredArgsConstructor
public class InterviewScheduleController {

    @Autowired
    InterviewScheduleService interviewScheduleService;

    @GetMapping("/candidate-recruitment/{candidateRecruitmentId}")
    public ResponseEntity<?> getInterviewInitData(@Valid @PathVariable Long candidateRecruitmentId) {
        InterviewScheduleDTO interviewScheduleDTO = interviewScheduleService.getInterviewByCandidateRecruitmentId(candidateRecruitmentId);
        return ResponseEntity.ok(interviewScheduleDTO);
    }

    @PostMapping
    public ResponseEntity<?> createInterviewSchedule(@Valid @RequestBody InterviewScheduleDTO dto) {
        InterviewScheduleDTO created = interviewScheduleService.createInterviewSchedule(dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

}