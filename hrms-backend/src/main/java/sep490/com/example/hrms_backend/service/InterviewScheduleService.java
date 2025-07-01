package sep490.com.example.hrms_backend.service;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sep490.com.example.hrms_backend.dto.InterviewScheduleDTO;
import sep490.com.example.hrms_backend.entity.*;
import sep490.com.example.hrms_backend.mapper.InterviewScheduleMapper;
import sep490.com.example.hrms_backend.repository.*;

import java.util.List;

@Service
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

    public InterviewScheduleDTO getInterviewByCandidateRecruitmentId(Long id) {
        CandidateRecruitment candidateRecruitment = candidateRecruitmentRepository.findById(id).orElse(null);
        Candidate candidate = candidateRecruitment.getCandidate();
        Recruitment recruitment = candidateRecruitment.getRecruitment();
        return InterviewScheduleDTO.builder().candidateId(candidate.getId())
                .candidateName(candidate.getCandidateName())
                .candidateEmail(candidate.getEmail())
                .candidatePhone(candidate.getPhoneNumber())
                .candidateGender(candidate.getGender())
                .dob(candidate.getDob())
                .recruitmentId(recruitment.getId())
                .recruitmentTitle(recruitment.getTitle())
                .recruitmentDescription(recruitment.getJobDescription())
                .recruitmentDepartment(recruitment.getDepartment().getDepartmentName())
                .jobRequirement(recruitment.getJobRequirement())
                .employmentType(recruitment.getEmploymentType())
                .benefits(recruitment.getBenefits())
                .build();
    }

    public InterviewScheduleDTO createInterviewSchedule(@Valid InterviewScheduleDTO interviewScheduleDTO) {
        Candidate candidate = candidateRepository.findById(interviewScheduleDTO.getCandidateId())
                .orElseThrow(() -> new RuntimeException("Ứng viên không tồn tại"));
        Employee interviewer = employeeRepository.findById(interviewScheduleDTO.getInterviewerId())
                .orElseThrow(() -> new RuntimeException("Người phỏng vấn không tồn tại"));
        Recruitment recruitment = recruitmentRepository.findById(interviewScheduleDTO.getRecruitmentId())
                .orElseThrow(() -> new RuntimeException("Đợt tuyển dụng không tồn tại"));

        InterviewSchedule interviewSchedule = InterviewScheduleMapper.mapToInterviewScheduleEntity(interviewScheduleDTO);
        interviewSchedule.setCandidate(candidate);
        interviewSchedule.setInterviewer(interviewer);
        interviewSchedule.setRecruitment(recruitment);
        InterviewSchedule savedInterviewSchedule = interviewScheduleRepository.save(interviewSchedule);
        return InterviewScheduleMapper.mapToInterviewScheduleDTO(savedInterviewSchedule);
    }

    public InterviewSchedule save(InterviewSchedule schedule) {
        return interviewScheduleRepository.save(schedule);
    }


    public List<InterviewScheduleDTO> getAllInterviewSchedule() {
        return InterviewScheduleMapper.maptoInterviewcheduleDTOList(interviewScheduleRepository.findAll());
    }

}