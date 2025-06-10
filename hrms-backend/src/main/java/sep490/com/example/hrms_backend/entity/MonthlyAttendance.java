package sep490.com.example.hrms_backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "monthly_attendance")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MonthlyAttendance {

    // 🧩 ====== THUỘC TÍNH (ATTRIBUTES: D1 → D31) ======

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "monthly_attendance_id")
    private Long id;

    @Lob @Column(name = "D1")  private String D1;
    @Lob @Column(name = "D2")  private String D2;
    @Lob @Column(name = "D3")  private String D3;
    @Lob @Column(name = "D4")  private String D4;
    @Lob @Column(name = "D5")  private String D5;
    @Lob @Column(name = "D6")  private String D6;
    @Lob @Column(name = "D7")  private String D7;
    @Lob @Column(name = "D8")  private String D8;
    @Lob @Column(name = "D9")  private String D9;
    @Lob @Column(name = "D10") private String D10;
    @Lob @Column(name = "D11") private String D11;
    @Lob @Column(name = "D12") private String D12;
    @Lob @Column(name = "D13") private String D13;
    @Lob @Column(name = "D14") private String D14;
    @Lob @Column(name = "D15") private String D15;
    @Lob @Column(name = "D16") private String D16;
    @Lob @Column(name = "D17") private String D17;
    @Lob @Column(name = "D18") private String D18;
    @Lob @Column(name = "D19") private String D19;
    @Lob @Column(name = "D20") private String D20;
    @Lob @Column(name = "D21") private String D21;
    @Lob @Column(name = "D22") private String D22;
    @Lob @Column(name = "D23") private String D23;
    @Lob @Column(name = "D24") private String D24;
    @Lob @Column(name = "D25") private String D25;
    @Lob @Column(name = "D26") private String D26;
    @Lob @Column(name = "D27") private String D27;
    @Lob @Column(name = "D28") private String D28;
    @Lob @Column(name = "D29") private String D29;
    @Lob @Column(name = "D30") private String D30;
    @Lob @Column(name = "D31") private String D31;

    // 🔗 ====== QUAN HỆ (RELATIONSHIPS) ======

    // Thuộc về một nhân viên
    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;

    // Thuộc về một kỳ công (tháng/năm)
    @ManyToOne
    @JoinColumn(name = "attendance_period_id")
    private AttendancePeriod attendancePeriod;

    // Một bảng công tháng có nhiều bản ghi công theo ngày
    @OneToMany(mappedBy = "monthlyAttendance")
    private List<AttendanceLog> attendanceLogs;

    // Một bảng công tháng tương ứng với một bảng lương
    @OneToOne(mappedBy = "monthlyAttendance")
    private Salary salary;
}
