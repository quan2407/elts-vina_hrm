package sep490.com.example.hrms_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeOCRResponseDTO {
    private String employeeName;           // Họ và tên
    private String gender;                 // Giới tính
    private LocalDate dob;                 // Ngày sinh
    private String originPlace;            // Quê quán
    private String nationality;            // Quốc tịch
    private String citizenId;              // Số CCCD
    private LocalDate citizenIssueDate;    // Ngày cấp
    private LocalDate citizenExpiryDate;   // Ngày hết hạn
    private String address;                // Nơi thường trú

    private String faceBoundingBox;        // Bounding box của khuôn mặt từ ảnh mặt trước (nếu có)
    private String frontImagePath;
    private String backImagePath;
}
