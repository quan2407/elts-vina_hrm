package sep490.com.example.hrms_backend.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import sep490.com.example.hrms_backend.dto.EmployeeRequestDTO;
import sep490.com.example.hrms_backend.dto.EmployeeUpdateDTO;

import java.time.LocalDate;

public class StartWorkDateValidator implements ConstraintValidator<ValidStartWorkDate, Object> {

    @Override
    public boolean isValid(Object obj, ConstraintValidatorContext context) {
        LocalDate dob = null;
        LocalDate startDate = null;

        if (obj instanceof EmployeeUpdateDTO dto) {
            dob = dto.getDob();
            startDate = dto.getStartWorkAt();
        } else if (obj instanceof EmployeeRequestDTO dto) {
            dob = dto.getDob();
            startDate = dto.getStartWorkAt();
        }

        if (dob == null || startDate == null) {
            return true; // để @NotNull xử lý
        }
        if (startDate.isBefore(dob.plusYears(18))) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Ngày vào công ty phải sau ngày sinh ít nhất 18 năm")
                    .addPropertyNode("startWorkAt")
                    .addConstraintViolation();
            return false;
        }

        return true;
    }
}
