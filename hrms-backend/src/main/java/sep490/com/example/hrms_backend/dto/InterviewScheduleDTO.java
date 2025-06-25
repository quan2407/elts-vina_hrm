package sep490.com.example.hrms_backend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;


import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InterviewScheduleDTO {

    private Long id;
    @NotNull(message = "Thời gian phỏng vấn không được để trống"
    )
    private LocalDateTime scheduledAt;
    private String status;
    private String feedback;
    @NotNull(message = "Ứng viên không được để trống")
    private Long candidateId;
    @NotNull(message = "Người tuyển dụng không được để trống")
    private Long interviewerId;
    @NotNull(message = "Bài tuyển dụng không được để trống")
    private Long recrutmentId;
}
