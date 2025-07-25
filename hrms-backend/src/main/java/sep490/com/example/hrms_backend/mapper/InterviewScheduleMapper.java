package sep490.com.example.hrms_backend.mapper;

import sep490.com.example.hrms_backend.dto.InterviewScheduleDTO;
import sep490.com.example.hrms_backend.entity.InterviewSchedule;

import java.util.List;

public class InterviewScheduleMapper {

    public static InterviewScheduleDTO mapToInterviewScheduleDTO(InterviewSchedule interviewSchedule) {
        return InterviewScheduleDTO.builder()
                .id(interviewSchedule.getId())
                .scheduledAt(interviewSchedule.getScheduledAt())
                .status(interviewSchedule.getStatus())
                .feedback(interviewSchedule.getFeedback())
                .result(interviewSchedule.getResult())
                .candidateId(interviewSchedule.getCandidate().getId())
                .candidateName(interviewSchedule.getCandidate().getCandidateName())
                .candidateEmail(interviewSchedule.getCandidate().getEmail())
                .candidatePhone(interviewSchedule.getCandidate().getPhoneNumber())
                .candidateGender(interviewSchedule.getCandidate().getGender())
                .dob(interviewSchedule.getCandidate().getDob())
                .interviewerId(interviewSchedule.getInterviewer().getEmployeeId())
                .interviewerName(interviewSchedule.getInterviewer().getEmployeeName())
                .recruitmentId(interviewSchedule.getRecruitment().getId())
                .recruitmentTitle(interviewSchedule.getRecruitment().getTitle())
                .recruitmentDepartment(interviewSchedule.getRecruitment().getDepartment().getDepartmentName())
                .recruitmentDescription(interviewSchedule.getRecruitment().getJobDescription())
                .employmentType(interviewSchedule.getRecruitment().getEmploymentType())
                .jobRequirement(interviewSchedule.getRecruitment().getJobRequirement())
                .benefits(interviewSchedule.getRecruitment().getBenefits())
                .build();
    }

    public static List<InterviewScheduleDTO> maptoInterviewcheduleDTOList(List<InterviewSchedule> interviewScheduleList){
        return interviewScheduleList.stream().map(interviewSchedule -> {
           InterviewScheduleDTO dto = new InterviewScheduleDTO();
           return mapToInterviewScheduleDTO(interviewSchedule);
        }).toList();
    }

    public static InterviewSchedule mapToInterviewScheduleEntity(InterviewScheduleDTO interviewScheduleDTO) {
        return InterviewSchedule.builder()
                .scheduledAt(interviewScheduleDTO.getScheduledAt())
                .status(interviewScheduleDTO.getStatus())
                .feedback(interviewScheduleDTO.getFeedback())
                .build();
    }
}