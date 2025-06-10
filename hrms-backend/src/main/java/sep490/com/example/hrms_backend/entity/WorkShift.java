package sep490.com.example.hrms_backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalTime;
import java.util.List;

@Entity
@Table(name = "work_shift")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkShift {

    // 🧩 ====== THUỘC TÍNH (ATTRIBUTES) ======

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "work_shift_id")
    private Long id;

    @NotBlank
    @Column(name = "work_shift_name", nullable = false)
    private String name; // tên ca làm (ví dụ: Ca sáng, Ca tối)

    @NotNull
    @Column(name = "start_time")
    private LocalTime startTime; // thời gian bắt đầu ca

    @NotNull
    @Column(name = "end_time")
    private LocalTime endTime; // thời gian kết thúc ca

    // 🔗 ====== QUAN HỆ (RELATIONSHIPS) ======

    // Một ca làm có thể được sử dụng trong nhiều lịch làm việc
    @OneToMany(mappedBy = "workShift")
    private List<WorkSchedule> workSchedules;
}
