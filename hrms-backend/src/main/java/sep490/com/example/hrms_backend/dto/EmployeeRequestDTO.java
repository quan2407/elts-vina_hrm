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
    private String employeeName;

    @NotNull(message = "Giới tính không được để trống")
    @JsonDeserialize(using = GenderDeserializer.class)
    private Gender gender;

    @Past(message = "Ngày sinh phải trong quá khứ")
    private LocalDate dob;

    @Size(max = 255, message = "Nơi sinh không được vượt quá 255 ký tự")
    private String placeOfBirth;

    @Size(max = 255, message = "Nguyên quán không được vượt quá 255 ký tự")
    private String originPlace;

    @Size(max = 255, message = "Quốc tịch không được vượt quá 255 ký tự")
    private String nationality;

    @Pattern(regexp = "^[0-9]{9,12}$", message = "Số CMND/CCCD phải gồm 9-12 chữ số")
    private String citizenId; // ✅ Có thể null, nếu có thì phải đúng định dạng

    @PastOrPresent(message = "Ngày cấp CMND/CCCD phải là ngày trong quá khứ hoặc hiện tại")
    private LocalDate citizenIssueDate; // ✅ Có thể null

    @FutureOrPresent(message = "Ngày hết hạn CMND/CCCD phải là ngày hiện tại hoặc tương lai")
    private LocalDate citizenExpiryDate; // ✅ Có thể null

    @Size(max = 255, message = "Địa chỉ không được vượt quá 255 ký tự")
    private String address;

    @Size(max = 255, message = "Link ảnh không được vượt quá 255 ký tự")
    private String image;

    @PastOrPresent(message = "Ngày vào công ty phải trong quá khứ hoặc hiện tại")
    private LocalDate startWorkAt;

    @Pattern(regexp = "^[0-9\\-\\+]{9,15}$", message = "Số điện thoại không đúng định dạng")
    private String phoneNumber;

    @Email(message = "Email không đúng định dạng")
    private String email;

    @NotNull(message = "ID phòng ban không được để trống")
    private Long departmentId;

    @NotNull(message = "ID vị trí không được để trống")
    private Long positionId;

    @NotNull(message = "ID line không được để trống")
    private Long lineId;
}
