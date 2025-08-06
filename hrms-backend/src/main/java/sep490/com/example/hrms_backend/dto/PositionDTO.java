package sep490.com.example.hrms_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import sep490.com.example.hrms_backend.entity.Position;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PositionDTO {
    private Long id;
    private String name;

    public static PositionDTO fromEntity(Position p) {
        if (p == null) return null;
        return new PositionDTO(p.getPositionId(), p.getPositionName());
    }
}
