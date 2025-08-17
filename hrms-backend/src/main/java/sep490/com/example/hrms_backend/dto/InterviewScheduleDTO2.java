package sep490.com.example.hrms_backend.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import sep490.com.example.hrms_backend.enums.InterviewResult;
import sep490.com.example.hrms_backend.enums.InterviewScheduleStatus;


import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InterviewScheduleDTO2 {

    private Long id;
    @NotNull(message = "Thời gian phỏng vấn không được để trống")
    private LocalDateTime scheduledAt;
    private InterviewScheduleStatus status;
    private String feedback;
    @NotNull(message = "Ứng viên không được để trống")
    private Long candidateId;

    private InterviewResult result;

    private String candidateName;
    private String candidateEmail;
    private String candidatePhone;
    private String candidateGender;
    private LocalDate dob;

    @NotNull(message = "Người phỏng vấn không được để trống")
    private Long interviewerId;
    private String interviewerName;

    @NotNull(message = "Bài tuyển dụng không được để trống")
    private Long recruitmentId;
    private String recruitmentTitle;
    private String recruitmentDepartment;
    private String recruitmentDescription;
    private String employmentType;
    private String jobRequirement;
    private String benefits;

}
