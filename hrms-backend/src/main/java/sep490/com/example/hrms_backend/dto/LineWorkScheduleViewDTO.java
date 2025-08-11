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
    // ==== NEW: thông tin OT tháng (đơn vị phút/giờ) ====
    private Integer monthlyOtCapMinutes;        // mặc định 2400 (40h)
    private Integer monthlyOtUsedMinutes;       // đã dùng trong tháng
    private Integer monthlyOtRemainingMinutes;  // còn lại trong tháng
    private Double  monthlyOtRemainingHours;    // còn lại theo giờ (hiển thị)
}
