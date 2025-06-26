package sep490.com.example.hrms_backend.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.Data;
import sep490.com.example.hrms_backend.validation.Age18;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
public class CandidateDto {


    private Long id;

    @NotBlank(message = "Tên không đc trống")
    private String candidateName; // tên ứng viên

    @NotNull(message = "Giới tính không đc trống")
    private String gender; // giới tính

    @Age18(message = "Ứng viên phải đủ 18 tuổi")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy/MM/dd")
    @NotNull(message = "Ngày sinh không đc trống")
    private LocalDate dob; // ngày sinh

    @Pattern(regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$",
            message = "Email không đúng định dạng")
    private String email; // email ứng viên

    @Pattern(regexp = "^[0-9\\-\\+]{9,15}$", message = "Số điện thoại không hợp lệ")
    private String phoneNumber; // số điện thoại ứng viên



    // Một ứng viên ứng tuyển vào một đợt tuyển dụng

    private List<Long> recruitmentId = new ArrayList<>();

    // Một ứng viên có thể được phỏng vấn nhiều lần (1 - n)

    private List<Long> interviewScheduleId = new ArrayList<>();
}
