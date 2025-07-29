package sep490.com.example.hrms_backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import lombok.*;
import sep490.com.example.hrms_backend.enums.NotificationType;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "notification")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {



    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "Type" )
    private NotificationType type;

    @NotBlank
    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "isRead" )
    private Boolean isRead;

    @PastOrPresent
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @ManyToMany
    @JoinTable(
            name = "account_notification",
            joinColumns = @JoinColumn(name = "notification_id"),
            inverseJoinColumns = @JoinColumn(name = "account_id")
    )
    private Set<Account> account;
}
