package sep490.com.example.hrms_backend.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "work_schedule_detail")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkScheduleDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "work_schedule_detail_id")
    private Long id;

    @Column(name = "date_work", nullable = false)
    private LocalDate dateWork;
    @JsonFormat(pattern = "HH:mm")
    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;
    @JsonFormat(pattern = "HH:mm")
    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @Column(name = "is_overtime")
    private Boolean isOvertime;

    @ManyToOne
    @JoinColumn(name = "work_schedule_id", nullable = false)
    private WorkSchedule workSchedule;
}
