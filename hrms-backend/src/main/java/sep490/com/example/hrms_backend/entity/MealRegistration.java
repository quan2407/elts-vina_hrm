package sep490.com.example.hrms_backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "meal_registration")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MealRegistration {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "meal_id")
    private Long id;

    @NotNull
    @Column(name = "meal_date", nullable = false)
    private LocalDate mealDate;

    @PastOrPresent
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "attendance_log_id")
    private AttendanceLog attendanceLog;
}
