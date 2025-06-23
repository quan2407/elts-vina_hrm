package sep490.com.example.hrms_backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeOwnProfileUpdateDTO {
    @Size(max = 255, message = "Họ và tên không được vượt quá 255 ký tự")
    private String employeeName;

    @Size(max = 255, message = "Địa chỉ không được vượt quá 255 ký tự")
    private String address;

    @Pattern(regexp = "^[0-9\\-\\+]{9,15}$", message = "Số điện thoại không đúng định dạng")
    private String phoneNumber;

    @Email(message = "Email không đúng định dạng")
    private String email;

}
