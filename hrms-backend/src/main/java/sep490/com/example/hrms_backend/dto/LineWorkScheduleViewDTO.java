package sep490.com.example.hrms_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class LineWorkScheduleViewDTO {
    private Long lineId;
    private String lineName;
    private List<WorkScheduleDayDetailDTO> workDetails;
    private boolean isSubmitted;
    private boolean isAccepted;
    private String rejectReason;

}
