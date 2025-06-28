package sep490.com.example.hrms_backend.service;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import sep490.com.example.hrms_backend.dto.InterviewScheduleDTO;
import sep490.com.example.hrms_backend.entity.*;
import sep490.com.example.hrms_backend.mapper.InterviewScheduleMapper;
import sep490.com.example.hrms_backend.repository.*;

import java.util.List;

public class InterviewScheduleService {

    @Autowired
    InterviewScheduleRepository interviewScheduleRepository;

    @Autowired
    private CandidateRepository candidateRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private RecruitmentRepository recruitmentRepository;

    @Autowired
    private CandidateRecruitmentRepository candidateRecruitmentRepository;

    public InterviewScheduleDTO createInterviewSchedule(@Valid InterviewScheduleDTO interviewScheduleDTO) {
        Candidate candidate = candidateRepository.findById(interviewScheduleDTO.getCandidateId())
                .orElseThrow(() -> new RuntimeException("Ứng viên không tồn tại"));
        Employee interviewer = employeeRepository.findById(interviewScheduleDTO.getInterviewerId())
                .orElseThrow(() -> new RuntimeException("Người phỏng vấn không tồn tại"));
        Recruitment recruitment = recruitmentRepository.findById(interviewScheduleDTO.getRecrutmentId())
                .orElseThrow(() -> new RuntimeException("Đợt tuyển dụng không tồn tại"));

        InterviewSchedule interviewSchedule = new InterviewSchedule();
        interviewSchedule = InterviewScheduleMapper.mapToInterviewScheduleEntity(interviewScheduleDTO);
        interviewSchedule.setCandidate(candidate);
        interviewSchedule.setInterviewer(interviewer);
        interviewSchedule.setRecruitment(recruitment);
        InterviewSchedule savedInterviewSchedule = interviewScheduleRepository.save(interviewSchedule);
        return InterviewScheduleMapper.mapToInterviewScheduleDTO(savedInterviewSchedule);
    }

    public List<InterviewScheduleDTO> getAllInterviewSchedule() {
        return InterviewScheduleMapper.maptoInterviewcheduleDTOList(interviewScheduleRepository.findAll());
    }

    public InterviewScheduleDTO creaInterviewWithRecruitmentCandidate(@Valid InterviewScheduleDTO interviewScheduleDTO, Long id) {

        CandidateRecruitment candidateRecruitment = candidateRecruitmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Candidate Recruitment"));

        Candidate candidate = candidateRecruitment.getCandidate();

        Recruitment recruitment = candidateRecruitment.getRecruitment();

        Employee interviewer = employeeRepository.findById(interviewScheduleDTO.getInterviewerId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người phỏng vấn"));

        InterviewSchedule interviewSchedule = InterviewScheduleMapper.mapToInterviewScheduleEntity(interviewScheduleDTO);
        interviewSchedule.setCandidate(candidate);
        interviewSchedule.setInterviewer(interviewer);
        interviewSchedule.setRecruitment(recruitment);

        InterviewSchedule savedInterviewSchedule = interviewScheduleRepository.save(interviewSchedule);

        return InterviewScheduleMapper.mapToInterviewScheduleDTO(savedInterviewSchedule);
    }
}