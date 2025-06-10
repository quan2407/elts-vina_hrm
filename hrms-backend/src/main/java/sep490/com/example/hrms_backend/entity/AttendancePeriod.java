package sep490.com.example.hrms_backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "attendance_period")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttendancePeriod {

    // 🧩 ====== THUỘC TÍNH (ATTRIBUTES) ======

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "attendance_period_id")
    private Long id;

    @NotBlank
    @Column(name = "code", nullable = false, unique = true)
    private String code; // mã kỳ công (VD: T05-2025)

    @Min(1) @Max(12)
    @Column(name = "month", nullable = false)
    private int month; // tháng của kỳ công

    @Min(2000)
    @Column(name = "year", nullable = false)
    private int year; // năm của kỳ công

    @NotNull
    @Column(name = "start_date")
    private LocalDate startDate; // ngày bắt đầu kỳ công

    @Column(name = "is_locked")
    private Boolean isLocked; // cờ khoá kỳ công (không cho chỉnh sửa nữa)

    @Min(0)
    @Column(name = "số_nghỉ_phép")
    private Integer leaveDays; // số ngày nghỉ phép mặc định

    // 🔗 ====== QUAN HỆ (RELATIONSHIPS) ======

    // Một kỳ công có nhiều bảng công tháng của các nhân viên
    @OneToMany(mappedBy = "attendancePeriod")
    private List<MonthlyAttendance> monthlyAttendances;
}
