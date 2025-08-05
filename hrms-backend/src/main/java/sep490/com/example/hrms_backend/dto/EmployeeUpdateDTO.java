package sep490.com.example.hrms_backend.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import sep490.com.example.hrms_backend.enums.Gender;
import sep490.com.example.hrms_backend.validation.ValidWorkDateRange;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ValidWorkDateRange
public class EmployeeUpdateDTO {

    @NotBlank(message = "Họ và tên không được để trống")
    @Size(max = 255, message = "Họ và tên không được vượt quá 255 ký tự")
    @Pattern(regexp = "^[^\\d]*$", message = "Họ và tên không được chứa số")
    private String employeeName;

    @NotNull(message = "Giới tính không được để trống")
    private Gender gender;

    @NotNull(message = "Ngày sinh không được để trống")
    @Past(message = "Ngày sinh phải trong quá khứ")
    private LocalDate dob;

    @NotBlank(message = "Nơi sinh không được để trống")
    @Size(max = 255, message = "Nơi sinh không được vượt quá 255 ký tự")
    private String placeOfBirth;

    @NotBlank(message = "Nguyên quán không được để trống")
    @Size(max = 255, message = "Nguyên quán không được vượt quá 255 ký tự")
    private String originPlace;

    @NotBlank(message = "Quốc tịch không được để trống")
    @Size(max = 255, message = "Quốc tịch không được vượt quá 255 ký tự")
    private String nationality;

    @NotBlank(message = "Số CMND/CCCD không được để trống")
    @Pattern(regexp = "^[0-9]{9,12}$", message = "Số CMND/CCCD phải gồm 9-12 chữ số")
    private String citizenId;

    @NotNull(message = "Ngày cấp CMND/CCCD không được để trống")
    @PastOrPresent(message = "Ngày cấp CMND/CCCD phải là ngày trong quá khứ hoặc hiện tại")
    private LocalDate citizenIssueDate;

    @NotNull(message = "Ngày hết hạn CMND/CCCD không được để trống")
    @FutureOrPresent(message = "Ngày hết hạn CMND/CCCD phải là ngày hiện tại hoặc tương lai")
    private LocalDate citizenExpiryDate;

    @NotBlank(message = "Địa chỉ không được để trống")
    @Size(max = 255, message = "Địa chỉ không được vượt quá 255 ký tự")
    private String address;
    @Size(max = 255, message = "Nơi ở hiện nay không được vượt quá 255 ký tự")
    @NotBlank(message = "Nơi ở hiện nay không được để trống")
    private String currentAddress;

    @Size(max = 255, message = "Dân tộc không được vượt quá 255 ký tự")
    @NotBlank(message = "Dân tộc không được để trống")
    private String ethnicity;

    @Size(max = 255, message = "Tôn giáo không được vượt quá 255 ký tự")
    @NotBlank(message = "Tôn giáo không được để trống")
    private String religion;

    @Size(max = 255, message = "Trình độ văn hóa không được vượt quá 255 ký tự")
    @NotBlank(message = "Trình độ văn hóa không được để trống")
    private String educationLevel;

    @Size(max = 255, message = "Trình độ chuyên môn không được vượt quá 255 ký tự")
    @NotBlank(message = "Trình độ chuyên môn không được để trống")
    private String specializedLevel;

    @Size(max = 255, message = "Ngoại ngữ không được vượt quá 255 ký tự")
    private String foreignLanguages;

    @Size(max = 255, message = "Loại hình đào tạo không được vượt quá 255 ký tự")
    private String trainingType;

    @Size(max = 255, message = "Chuyên ngành đào tạo không được vượt quá 255 ký tự")
    @NotBlank(message = "Chuyên ngành đào tạo không được để trống")
    private String trainingMajor;


    private String cccdFrontImage;
    private String cccdBackImage;


    @NotNull(message = "Ngày vào công ty không được để trống")
    private LocalDate startWorkAt;
    @NotNull(message = "Ngày ra công ty không được để trống")
    private LocalDate endWorkAt;
    @NotBlank(message = "Số điện thoại không được để trống")
    @Pattern(regexp = "^[0-9\\-\\+]{9,15}$", message = "Số điện thoại không đúng định dạng")
    private String phoneNumber;

    @Email(message = "Email không đúng định dạng")
    @NotBlank(message = "Email không được để trống")
    private String email;
    @NotNull(message = "Lương cơ bản không được để trống")
    @DecimalMin(value = "0.0", inclusive = false, message = "Lương cơ bản phải là số lớn hơn 0")
    private BigDecimal basicSalary;
    @NotNull(message = "ID phòng ban không được để trống")
    private Long departmentId;

    @NotNull(message = "Vị trí không được để trống")
    private Long positionId;

}
