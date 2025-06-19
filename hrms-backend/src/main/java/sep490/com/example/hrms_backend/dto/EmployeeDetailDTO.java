package sep490.com.example.hrms_backend.dto;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeDetailDTO {
    private Long employeeId;
    private String employeeCode;
    private String employeeName;
    private String gender;
    private LocalDate dob;
    private String placeOfBirth;
    private String originPlace;
    private String nationality;
    private String citizenId;
    private LocalDate citizenIssueDate;
    private LocalDate citizenExpiryDate;
    private String citizenIssuePlace;
    private String address;
    private String image;
    private LocalDate startWorkAt;
    private String phoneNumber;
    private String email;

    private String departmentName;
    private String positionName;
    private String lineName;
}
