package sep490.com.example.hrms_backend.dto.benefit;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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



    private Boolean isActive;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
