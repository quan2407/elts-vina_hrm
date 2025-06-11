package sep490.com.example.hrms_backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "employee")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Employee {

    // 🧩 ====== THUỘC TÍNH (ATTRIBUTES) ======

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "employee_id")
    private Long employeeId;

    @NotBlank
    @Column(name = "employee_code", nullable = false, unique = true)
    private String employeeCode; // mã nhân viên

    @NotBlank
    @Column(name = "employee_name", nullable = false)
    private String employeeName; // họ tên nhân viên

    @NotBlank
    @Column(name = "gender", nullable = false)
    private String gender; // giới tính

    @Past
    @Column(name = "dob")
    private LocalDate dob; // ngày sinh

    @Column(name = "place_of_birth")
    private String placeOfBirth; // nơi sinh

    @Column(name = "image")
    private String image; // link ảnh

    @Column(name = "nationality")
    private String nationality; // quốc tịch

    @Column(name = "address")
    private String address; // địa chỉ hiện tại

    @PastOrPresent
    @Column(name = "start_work_at")
    private LocalDate startWorkAt; // ngày bắt đầu làm việc

    @Pattern(regexp = "^[0-9\\-\\+]{9,15}$", message = "Invalid phone number format")
    @Column(name = "phone_number")
    private String phoneNumber; // số điện thoại

    @Column(name = "citizen_id", unique = true)
    private String citizenId; // CCCD

    // 🔗 ====== QUAN HỆ (RELATIONSHIPS) ======

    // Nhân viên thuộc một line cụ thể
    @ManyToOne
    @JoinColumn(name = "line_id")
    private Line line;

    // Nhân viên thuộc một phòng ban
    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department;

    // Một nhân viên có một tài khoản hệ thống
    @OneToOne(mappedBy = "employee")
    private Account account;

    // Một nhân viên có thể là người phỏng vấn trong nhiều buổi
    @OneToMany(mappedBy = "interviewer")
    private List<InterviewSchedule> interviewSchedules;

    // Một nhân viên có thể là người tạo nhiều đợt tuyển dụng
    @OneToMany(mappedBy = "createdBy")
    private List<Recruitment> createdRecruitments;

    // Một nhân viên có thể gửi nhiều đơn từ
    @OneToMany(mappedBy = "employee")
    private List<Application> applications;

    // Một nhân viên có thể phê duyệt nhiều đơn từ ở các bước
    @OneToMany(mappedBy = "approver")
    private List<ApplicationApprovalStep> approvalSteps;

    // Một nhân viên có thể đăng ký nhiều phúc lợi
    @OneToMany(mappedBy = "employee")
    private List<BenefitRegistration> benefitRegistrations;

    // Một nhân viên có nhiều chấm công tháng
    @OneToMany(mappedBy = "employee")
    private List<MonthlyAttendance> monthlyAttendances;

    // Một nhân viên có thể có nhiều lịch làm việc
    @OneToMany(mappedBy = "employee")
    private List<WorkSchedule> workSchedules;
}
