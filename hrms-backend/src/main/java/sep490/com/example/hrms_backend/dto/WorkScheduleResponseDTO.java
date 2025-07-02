package sep490.com.example.hrms_backend.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkScheduleResponseDTO {
    private Long id;
    private int month;
    private int year;
    private Long lineId;
    private String lineName;
    private String departmentId;
    private String departmentName;
}
