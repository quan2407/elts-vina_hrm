package sep490.com.example.hrms_backend.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.validation.constraints.*;
import lombok.*;
import sep490.com.example.hrms_backend.enums.Gender;
import sep490.com.example.hrms_backend.enums.GenderDeserializer;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeRequestDTO {

    @NotBlank(message = "Mã nhân viên không được để trống")
    @Pattern(regexp = "^ELTSSX\\d{4}$", message = "Mã nhân viên phải theo định dạng ELTSSXxxxx")
    private String employeeCode;

    @NotBlank(message = "Họ và tên không được để trống")
    @Size(max = 255, message = "Họ và tên không được vượt quá 255 ký tự")
    @Pattern(regexp = "^[^\\d]*$", message = "Họ và tên không được chứa số")
    private String employeeName;

    @NotNull(message = "Giới tính không được để trống")
    @JsonDeserialize(using = GenderDeserializer.class)
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

    @Size(max = 255, message = "Link ảnh không được vượt quá 255 ký tự")
    private String image;

    @NotNull(message = "Ngày vào công ty không được để trống")
    @PastOrPresent(message = "Ngày vào công ty phải trong quá khứ hoặc hiện tại")
    private LocalDate startWorkAt;

    @Pattern(regexp = "^[0-9\\-\\+]{9,15}$", message = "Số điện thoại không đúng định dạng")
    @NotBlank(message = "Số điện thoại không được để trống")
    private String phoneNumber;

    @Email(message = "Email không đúng định dạng")
    @NotBlank(message = "Email không được để trống")
    private String email;

    @NotNull(message = "ID phòng ban không được để trống")
    private Long departmentId;

    @NotNull(message = "Vị trí không được để trống")
    private Long positionId;

}
