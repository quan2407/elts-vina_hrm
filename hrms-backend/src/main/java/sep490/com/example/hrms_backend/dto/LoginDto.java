package sep490.com.example.hrms_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginDto {
    @NotBlank(message = "Tên đăng nhập hoặc email không được để trống")
    private String usernameOrEmail;
    @NotBlank(message = "Mật khẩu không được để trống")
    @Size(min = 4, max = 50, message = "Mật khẩu phải từ 4 đến 50 ký tự")
    private String password;
}
