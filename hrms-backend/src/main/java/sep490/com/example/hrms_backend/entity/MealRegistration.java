package sep490.com.example.hrms_backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "meal_registration")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MealRegistration {

    // 🧩 ====== THUỘC TÍNH (ATTRIBUTES) ======

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "meal_id")
    private Long id;

    @NotNull
    @Column(name = "meal_date", nullable = false)
    private LocalDate mealDate; // ngày đăng ký ăn ca

    @PastOrPresent
    @Column(name = "created_at")
    private LocalDateTime createdAt; // thời điểm đăng ký

    // 🔗 ====== QUAN HỆ (RELATIONSHIPS) ======

    // Mỗi đăng ký ăn ca liên kết với một bản ghi chấm công
    @ManyToOne
    @JoinColumn(name = "attendance_log_id")
    private AttendanceLog attendanceLog;
}
