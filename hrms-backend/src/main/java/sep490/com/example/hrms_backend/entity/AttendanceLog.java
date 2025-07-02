package sep490.com.example.hrms_backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.PastOrPresent;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Entity
@Table(name = "attendance_log")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttendanceLog {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "attendance_log_id")
    private Long id;

    @PastOrPresent
    @Column(name = "date")
    private LocalDate date;
    @Column(name = "check_in")
    private LocalTime checkIn;

    @Column(name = "check_out")
    private LocalTime checkOut;


    @Column(name = "overtime_out")
    private LocalTime overtimeOut;

    @Column(name = "note")
    private String note;

    @ManyToOne
    @JoinColumn(name = "monthly_attendance_id")
    private AttendanceRecord attendanceRecord;

    @ManyToOne
    @JoinColumn(name = "day_attendance_type_id")
    private AttendanceType dayAttendanceType;

    @ManyToOne
    @JoinColumn(name = "night_attendance_type_id")
    private AttendanceType nightAttendanceType;

    @OneToMany(mappedBy = "attendanceLog")
    private List<MealRegistration> mealRegistrations;
}
