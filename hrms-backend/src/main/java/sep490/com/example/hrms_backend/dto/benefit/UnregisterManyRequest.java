package sep490.com.example.hrms_backend.dto.benefit;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UnregisterManyRequest {
    @NotNull
    @NotEmpty
    private List<Long> employeeIds;

}
