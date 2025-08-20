package sep490.com.example.hrms_backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
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

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "employee_id")
    private Long employeeId;

    @NotBlank
    @Column(name = "employee_code", nullable = false, unique = true)
    private String employeeCode;

    @Column(name = "image")
    private String image;

    @NotBlank
    @Column(name = "employee_name", nullable = false)
    private String employeeName;

    @NotBlank
    @Column(name = "gender", nullable = false)
    private String gender;

    @Past
    @Column(name = "dob")
    private LocalDate dob;

    @Column(name = "place_of_birth")
    private String placeOfBirth;

    @Column(name = "origin_place")
    private String originPlace;

    @Column(name = "nationality")
    private String nationality;

    @Column(name = "citizen_id", unique = true)
    private String citizenId;

    @PastOrPresent
    @Column(name = "citizen_issue_date")
    private LocalDate citizenIssueDate;

    @Column(name = "citizen_expiry_date")
    private LocalDate citizenExpiryDate;

    @Column(name = "citizen_issue_place")
    private String citizenIssuePlace;
    @Column(name = "address")
    private String address;
    @Column(name = "current_address")
    private String currentAddress;

    @Column(name = "ethnicity")
    private String ethnicity;

    @Column(name = "religion")
    private String religion;

    @Column(name = "education_level")
    private String educationLevel;

    @Column(name = "specialized_level")
    private String specializedLevel;

    @Column(name = "foreign_languages")
    private String foreignLanguages;

    @Column(name = "training_type")
    private String trainingType;

    @Column(name = "training_major")
    private String trainingMajor;


    @Column(name = "cccd_front_image")
    private String cccdFrontImage;
    @Column(name = "cccd_back_image")
    private String cccdBackImage;

    @Column(name = "start_work_at")
    private LocalDate startWorkAt;

    @Column(name = "end_work_at")
    private LocalDate endWorkAt;

    @Pattern(regexp = "^[0-9\\-\\+]{9,15}$", message = "Invalid phone number format")
    @Column(name = "phone_number")
    private String phoneNumber;

    @Email
    @Column(name = "email")
    private String email;

    @DecimalMin(value = "0.0", inclusive = true, message = "Lương cơ bản không hợp lệ")
    @Column(name = "basic_salary")
    private BigDecimal basicSalary;


    @ManyToOne
    @JoinColumn(name = "line_id")
    private Line line;

    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department;

    @ManyToOne
    @JoinColumn(name = "position_id")
    private Position position;

    @OneToOne(mappedBy = "employee")
    private Account account;

    @OneToMany(mappedBy = "interviewer")
    private List<InterviewSchedule> interviewSchedules;

    @OneToMany(mappedBy = "createdBy")
    private List<Recruitment> createdRecruitments;

    @OneToMany(mappedBy = "employee")
    private List<Application> applications;

    @OneToMany(mappedBy = "approver")
    private List<ApplicationApprovalStep> approvalSteps;

    @OneToMany(mappedBy = "employee")
    private List<BenefitRegistration> benefitRegistrations;

    @OneToMany(mappedBy = "employee")
    private List<AttendanceRecord> attendanceRecords;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted = false;

    @Column(name = "is_account_requested")
    private Boolean isAccountRequested;


    public String getCitizenIssuePlaceSafe() {
        return citizenIssuePlace != null ? citizenIssuePlace : "";
    }

    public String getOriginPlaceSafe() {
        return originPlace != null ? originPlace : "";
    }

    public String getNationalitySafe() {
        return nationality != null ? nationality : "";
    }

    public String getPhoneNumberSafe() {
        return phoneNumber != null ? phoneNumber : "";
    }

    public String getEmailSafe() {
        return email != null ? email : "";
    }

}
