package sep490.com.example.hrms_backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "password_reset_requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PasswordResetRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "employee_id")
    private Long employeeId;

    @Column(name = "employee_code")
    private String employeeCode;

    @Column(name = "employee_name")
    private String employeeName;

    @Column(name = "department_name")
    private String departmentName;

    @Column(name = "position_name")
    private String positionName;

    @Column(name = "line_name")
    private String lineName;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private boolean approved;

    private LocalDateTime requestedAt;

    private LocalDateTime approvedAt;
}
