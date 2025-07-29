package sep490.com.example.hrms_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SimpleEmployeeDTO {
    private Long id;
    private String code;
    private String name;
    private String position;
    private String department;
    private String line;
}
