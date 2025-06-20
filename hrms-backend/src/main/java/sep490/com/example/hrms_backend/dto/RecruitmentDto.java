package sep490.com.example.hrms_backend.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import sep490.com.example.hrms_backend.entity.Candidate;
import sep490.com.example.hrms_backend.entity.CandidateRecruitment;
import sep490.com.example.hrms_backend.entity.Department;
import sep490.com.example.hrms_backend.entity.Employee;
import sep490.com.example.hrms_backend.enums.RecruitmentStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class RecruitmentDto {
    private Long recruitmentId;

    @NotBlank(message = "Tiêu đề không đc trống")
    private String title; // tiêu đề đợt tuyển dụng
    @NotBlank(message = "Địa điểm làm việc không đc trống")

    private String workLocation; // địa điôngm làm việc
    @NotBlank(message = "Loại hình công việc không đc trống")

    private String employmentType; // loại hình (toàn thời gian, part-time,...)
    @NotBlank(message = "Mô tả công việc không đc trống")

    private String jobDescription; // mô tả công việc

    private String jobRequirement; // yêu cầu tuyển dụng

    private String benefits; // quyền lợi
    @NotBlank(message = "Khoảng lương không đc trống")

    private String salaryRange; // khoảng lương
    @NotNull(message = "Số lượng tuyển dụng không đc trống")
    @Min(value = 1, message = "Số lượng tuyển dụng phải lớn hơn 0")
    private Integer quantity; // số lượng cần tuyển
    @NotNull(message = "Hạn tuyển dụng không đc trống")
    @FutureOrPresent(message = "Hạn tuyển dụng phải là ngày trong tương lai")
    private LocalDateTime expiredAt; // ngày hết hạn tuyển dụng

    @PastOrPresent
    private LocalDateTime createAt; // ngày tạo

    @PastOrPresent
    private LocalDateTime updateAt; // ngày cập nhật

    private RecruitmentStatus status; // trạng thái (đang mở, đã đóng, v.v.)

    // 🔗 ====== QUAN HỆ (RELATIONSHIPS) ======

    // Mỗi đợt tuyển dụng thuộc về 1 phòng ban
    @NotNull(message = "Phòng ban không đc trống")
    private Long departmentId;

    // Người tạo là một nhân viên cụ thể

    private Long createdById;


    private List<Long> candidateRecruitmentsId = new ArrayList<>();

}
