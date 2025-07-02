package sep490.com.example.hrms_backend.entity;

import jakarta.persistence.*;
        import lombok.*;

        import java.time.LocalDate;

@Entity
@Table(name = "attendance_record")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttendanceRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "attendance_record_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @ManyToOne
    @JoinColumn(name = "work_schedule_id", nullable = false)
    private WorkSchedule workSchedule;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "att_month", nullable = false)
    private int month;

    @Column(name = "att_year", nullable = false)
    private int year;

    @Column(name = "day_shift")
    private String dayShift;

    @Column(name = "ot_shift")
    private String otShift;

    @Column(name = "weekend_shift")
    private String weekendShift;

    @Column(name = "holiday_shift")
    private String holidayShift;
}

