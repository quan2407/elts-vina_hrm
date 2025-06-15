package sep490.com.example.hrms_backend.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.util.List;

@Data
public class CandidateDto {


    @NotNull(message = "Id không đc trống")
    private Long id;

    @NotNull(message = "Candidate name không đc trống")
    private String candidateName; // tên ứng viên

    @NotNull(message = "Email không đc trống")
    @Pattern(regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$",
            message = "Email không đúng định dạng")
    private String email; // email ứng viên

    @Pattern(regexp = "^[0-9\\-\\+]{9,15}$", message = "Phone number không hợp lệ")
    private String phoneNumber; // số điện thoại ứng viên

//    private String note; // ghi chú nội bộ
//
//    @NotNull(message = "Status không được trống")
//    private CandidateStatus status; // trạng thái ứng viên (ví dụ: đang xét, bị loại,...)
//
//
//    private LocalDateTime submittedAt; // thời điểm ứng viên ứng tuyển

    // 🔗 ====== QUAN HỆ (RELATIONSHIPS) ======

    // Một ứng viên ứng tuyển vào một đợt tuyển dụng

    private Long recruitmentId;

    // Một ứng viên có thể được phỏng vấn nhiều lần (1 - n)

    private List<Long> interviewScheduleId;
}
