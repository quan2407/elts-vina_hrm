package sep490.com.example.hrms_backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "system_log")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SystemLog {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "system_log_id")
    private Long id;

    @NotBlank
    @Column(name = "action")
    private String action;

    @NotBlank
    @Column(name = "table_name", nullable = false)
    private String tableName;


    @Column(name = "attribute_name")
    private String attribute;

    @PastOrPresent
    @Column(name = "action_at")
    private LocalDateTime actionAt;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;
}
