package sep490.com.example.hrms_backend.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttendanceCellDTO {
    private String shift;
    private String overtime;
    private String weekend;
    private String holiday;
}
