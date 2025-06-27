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
                .candidateId(interviewSchedule.getCandidate().getId())
                .interviewerId(interviewSchedule.getInterviewer().getEmployeeId())
                .recrutmentId(interviewSchedule.getRecruitment().getId())
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