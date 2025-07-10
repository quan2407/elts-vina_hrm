package sep490.com.example.hrms_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LineDTO {
    private Long id;
    private String name;
    private Long leaderId;

    public LineDTO(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
