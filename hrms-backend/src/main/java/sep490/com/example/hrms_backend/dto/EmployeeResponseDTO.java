package sep490.com.example.hrms_backend.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;

@Data
@Builder
public class EmployeeResponseDTO {
    private Long employeeId;
    private String employeeCode;
    private String employeeName;
    private String gender;
    private LocalDate dob;
    private String placeOfBirth;
    private String nationality;
    private String address;
    private LocalDate startWorkAt;
    private String phoneNumber;
    private String citizenId;
    private String departmentName;
    private String lineName;
    private String positionName;
    private String accountUsername;
}
