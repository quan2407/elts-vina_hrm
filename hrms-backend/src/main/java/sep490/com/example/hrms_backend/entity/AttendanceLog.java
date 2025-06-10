package sep490.com.example.hrms_backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.PastOrPresent;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Entity
@Table(name = "attendance_log")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttendanceLog {

    // 🧩 ====== THUỘC TÍNH (ATTRIBUTES) ======

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "attendance_log_id")
    private Long id;

    @PastOrPresent
    @Column(name = "date")
    private LocalDate date; // ngày làm việc

    @Column(name = "check_in")
    private LocalTime checkIn;

    @Column(name = "check_out")
    private LocalTime checkOut;


    @Column(name = "overtime_out")
    private LocalTime overtimeOut; // giờ tăng ca kết thúc (nếu có)

    @Column(name = "note")
    private String note; // ghi chú chấm công

    // 🔗 ====== QUAN HỆ (RELATIONSHIPS) ======

    // Chấm công này thuộc về một bảng chấm công tháng
    @ManyToOne
    @JoinColumn(name = "monthly_attendance_id")
    private MonthlyAttendance monthlyAttendance;

    // Loại công áp dụng cho ban ngày
    @ManyToOne
    @JoinColumn(name = "day_attendance_type_id")
    private AttendanceType dayAttendanceType;

    // Loại công áp dụng cho ban đêm (nếu có)
    @ManyToOne
    @JoinColumn(name = "night_attendance_type_id")
    private AttendanceType nightAttendanceType;

    // Một bản ghi chấm công có thể có nhiều đăng ký ăn ca
    @OneToMany(mappedBy = "attendanceLog")
    private List<MealRegistration> mealRegistrations;
}
