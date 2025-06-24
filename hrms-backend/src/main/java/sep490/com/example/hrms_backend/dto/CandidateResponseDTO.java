package sep490.com.example.hrms_backend.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import sep490.com.example.hrms_backend.enums.CandidateStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class CandidateResponseDTO {

    private Long id;

    @NotBlank(message = "Tên không đc trống")
    private String candidateName; // tên ứng viên

    @NotNull(message = "Giới tính không đc trống")
    private String gender; // giới tính

    @Past(message = "Ngày sinh trong quá khứ!")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy/MM/dd")
    @NotNull(message = "Ngày sinh không đc trống")
    private LocalDate dob; // ngày sinh

    @Pattern(regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$",
            message = "Email không đúng định dạng")
    private String email; // email ứng viên

    @Pattern(regexp = "^[0-9\\-\\+]{9,15}$", message = "Số điện thoại không hợp lệ")
    private String phoneNumber; // số điện thoại ứng viên

    // Thông tin về việc ứng tuyển
    private CandidateStatus status;
    private LocalDateTime submittedAt;
    private String note;

    private Long recruitmentId;
    private Long candidateRecruitmentId;


}
