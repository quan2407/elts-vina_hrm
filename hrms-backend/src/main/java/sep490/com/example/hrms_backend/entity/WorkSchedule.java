package sep490.com.example.hrms_backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.FutureOrPresent;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "work_schedule")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkSchedule {

    // 🧩 ====== THUỘC TÍNH (ATTRIBUTES) ======

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "work_schedule_id")
    private Long id;

    @FutureOrPresent
    @Column(name = "date_work")
    private LocalDate dateWork; // ngày làm việc cụ thể

    @Column(name = "note")
    private String note; // ghi chú lịch làm (nếu có)

    // 🔗 ====== QUAN HỆ (RELATIONSHIPS) ======

    // Lịch làm thuộc về một nhân viên cụ thể
    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;

    // Lịch làm ứng với một ca làm cụ thể
    @ManyToOne
    @JoinColumn(name = "work_shift_id")
    private WorkShift workShift;
}
