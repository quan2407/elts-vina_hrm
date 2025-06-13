package sep490.com.example.hrms_backend.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Data;
import sep490.com.example.hrms_backend.entity.Candidate;
import sep490.com.example.hrms_backend.entity.Department;
import sep490.com.example.hrms_backend.entity.Employee;
import sep490.com.example.hrms_backend.enums.RecruitmentStatus;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class RecruitmentDto {
    @NotEmpty(message = "id không đc trống")
    private Long recruitmentId;

    @NotEmpty(message = "Recruitment title không đc trống")
    private String title; // tiêu đề đợt tuyển dụng

    private String workLocation; // địa điôngm làm việc

    private String employmentType; // loại hình (toàn thời gian, part-time,...)

    private String jobDescription; // mô tả công việc

    private String jobRequirement; // yêu cầu tuyển dụng

    private String benefits; // quyền lợi

    private String salaryRange; // khoảng lương

    @Min(value = 1, message = "Quantity phải lớn hơn 0")
    private Integer quantity; // số lượng cần tuyển

    private LocalDateTime expiredAt; // ngày hết hạn tuyển dụng

    @PastOrPresent
    private LocalDateTime createAt; // ngày tạo

    @PastOrPresent
    private LocalDateTime updateAt; // ngày cập nhật

    private RecruitmentStatus status; // trạng thái (đang mở, đã đóng, v.v.)

    // 🔗 ====== QUAN HỆ (RELATIONSHIPS) ======

    // Mỗi đợt tuyển dụng thuộc về 1 phòng ban

    private String departmentName;

    // Người tạo là một nhân viên cụ thể

    private String createdByIdName;

    // Một đợt tuyển dụng có nhiều ứng viên
     private List<String> candidate_id;
}
