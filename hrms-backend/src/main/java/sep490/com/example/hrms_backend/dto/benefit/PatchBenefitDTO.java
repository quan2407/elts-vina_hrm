package sep490.com.example.hrms_backend.dto.benefit;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
@Data
@NoArgsConstructor
@AllArgsConstructor

public class PatchBenefitDTO {
    private Long id;


    private String title;

    private String description;

    private LocalDate startDate;

    private LocalDate endDate;

    private String detail;

    private Integer maxParticipants;

    private Boolean isActive;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
