package sep490.com.example.hrms_backend.service;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import sep490.com.example.hrms_backend.dto.InterviewScheduleDTO;
import sep490.com.example.hrms_backend.entity.Candidate;
import sep490.com.example.hrms_backend.entity.Employee;
import sep490.com.example.hrms_backend.entity.InterviewSchedule;
import sep490.com.example.hrms_backend.entity.Recruitment;
import sep490.com.example.hrms_backend.mapper.InterviewScheduleMapper;
import sep490.com.example.hrms_backend.repository.CandidateRepository;
import sep490.com.example.hrms_backend.repository.EmployeeRepository;
import sep490.com.example.hrms_backend.repository.InterviewScheduleRepository;
import sep490.com.example.hrms_backend.repository.RecruitmentRepository;

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

    public List<InterviewScheduleDTO> getAllInterviewSchedule(){
        return InterviewScheduleMapper.maptoInterviewcheduleDTOList(interviewScheduleRepository.findAll());
    }
}