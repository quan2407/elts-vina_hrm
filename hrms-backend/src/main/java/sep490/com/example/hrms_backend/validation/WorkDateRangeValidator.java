package sep490.com.example.hrms_backend.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import sep490.com.example.hrms_backend.dto.EmployeeRequestDTO;
import sep490.com.example.hrms_backend.dto.EmployeeUpdateDTO;

import java.time.LocalDate;

public class WorkDateRangeValidator implements ConstraintValidator<ValidWorkDateRange, Object> {

    @Override
    public boolean isValid(Object obj, ConstraintValidatorContext context) {
        LocalDate startDate = null;
        LocalDate endDate = null;

        if (obj instanceof EmployeeUpdateDTO dto) {
            startDate = dto.getStartWorkAt();
            endDate = dto.getEndWorkAt();
        } else if (obj instanceof EmployeeRequestDTO dto) {
            startDate = dto.getStartWorkAt();
            endDate = dto.getEndWorkAt();
        }

        if (startDate == null || endDate == null) {
            return true; // Let @NotNull handle
        }

        if (!endDate.isAfter(startDate)) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Ngày nghỉ phải sau ngày vào công ty")
                    .addPropertyNode("endWorkAt")
                    .addConstraintViolation();
            return false;
        }

        return true;
    }
}

