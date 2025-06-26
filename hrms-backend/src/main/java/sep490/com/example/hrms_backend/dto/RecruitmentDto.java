package sep490.com.example.hrms_backend.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import sep490.com.example.hrms_backend.entity.Candidate;
import sep490.com.example.hrms_backend.entity.CandidateRecruitment;
import sep490.com.example.hrms_backend.entity.Department;
import sep490.com.example.hrms_backend.entity.Employee;
import sep490.com.example.hrms_backend.enums.RecruitmentStatus;
import sep490.com.example.hrms_backend.validation.ValidSalaryRange;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@ValidSalaryRange
@Data
public class RecruitmentDto {
    private Long recruitmentId;

    @NotBlank(message = "Tiêu đề không được trống")
    private String title; // tiêu đề đợt tuyển dụng
    @NotBlank(message = "Địa điểm làm việc không được trống")

    private String workLocation; // địa điểm làm việc
    @NotBlank(message = "Loại hình công việc không được trống")

    private String employmentType; // loại hình (toàn thời gian, part-time,...)

    @NotBlank(message = "Mô tả công việc không được trống")
    private String jobDescription; // mô tả công việc

    private String jobRequirement; // yêu cầu tuyển dụng

    private String benefits; // quyền lợi

    @NotNull(message = "Mức lương tối thiểu không được trống")
    @Min(value = 1, message = "Số lượng tuyển dụng phải lớn hơn 0")
    private Long minSalary;

    @NotNull(message = "Mức lương tối đa không được trống")
    @Min(value = 1, message = "Số lượng tuyển dụng phải lớn hơn 0")
    private Long maxSalary;

    @NotNull(message = "Số lượng tuyển dụng không được trống")
    @Min(value = 1, message = "Số lượng tuyển dụng phải lớn hơn 0")
    private Integer quantity; // số lượng cần tuyển
    @NotNull(message = "Hạn tuyển dụng không được trống")
    @FutureOrPresent(message = "Hạn tuyển dụng phải là ngày trong tương lai")
    private LocalDateTime expiredAt; // ngày hết hạn tuyển dụng

    @PastOrPresent
    private LocalDateTime createAt; // ngày tạo

    @PastOrPresent
    private LocalDateTime updateAt; // ngày cập nhật
//    @NotNull(message = "Trạng thái tuyển dụng không được để trống")
    private RecruitmentStatus status; // trạng thái (đang mở, đã đóng, v.v.)

    // 🔗 ====== QUAN HỆ (RELATIONSHIPS) ======

    // Mỗi đợt tuyển dụng thuộc về 1 phòng ban
    @NotNull(message = "Phòng ban không được trống")
    private Long departmentId;

    // Người tạo là một nhân viên cụ thể

    private Long createdById;


    private List<Long> candidateRecruitmentsId = new ArrayList<>();

}
