package sep490.com.example.hrms_backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "candidate")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Candidate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "candidate_id")
    private Long id;

    @NotBlank
    @Column(name = "candidate_name", nullable = false)
    private String candidateName;

    @NotBlank
    @Column(name = "gender", nullable = false)
    private String gender;

    @Past
    @Column(name = "dob")
    private LocalDate dob;

    @Email
    @Column(name = "email")
    private String email;

    @Pattern(regexp = "^[0-9\\-\\+]{9,15}$")
    @Column(name = "phone_number")
    private String phoneNumber;



    @OneToMany(mappedBy = "candidate", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CandidateRecruitment> candidateRecruitments = new ArrayList<>();

    @OneToMany(mappedBy = "candidate", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InterviewSchedule> interviewSchedules = new ArrayList<>();
}
