package sep490.com.example.hrms_backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "work_schedule")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "work_schedule_id")
    private Long id;

    @Column(name = "month", nullable = false)
    private int month;

    @Column(name = "year", nullable = false)
    private int year;

    @ManyToOne
    @JoinColumn(name = "line_id")
    private Line line;

    @ManyToOne
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    @OneToMany(mappedBy = "workSchedule", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WorkScheduleDetail> workScheduleDetails;
    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted = false;
    @Column(name = "is_accepted", nullable = false)
    private boolean isAccepted = false;
    @Column(name = "is_submitted", nullable = false)
    private boolean isSubmitted = false;
    @Column(name = "reject_reason")
    private String rejectReason;
    @Column(name = "need_revision", nullable = false)
    private boolean needRevision = false;
    @Column(name = "monthly_ot_cap_minutes", nullable = false)
    @Builder.Default
    private int monthlyOtCapMinutes = 40 * 60;

    @Column(name = "monthly_ot_used_minutes", nullable = false)
    @Builder.Default
    private int monthlyOtUsedMinutes = 0;
    @Transient
    public int getMonthlyOtRemainingMinutes() {
        return Math.max(0, monthlyOtCapMinutes - monthlyOtUsedMinutes);
    }
    @Transient
    public double getMonthlyOtRemainingHours() {
        return getMonthlyOtRemainingMinutes() / 60.0;
    }

}
