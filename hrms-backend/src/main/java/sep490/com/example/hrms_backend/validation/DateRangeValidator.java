package sep490.com.example.hrms_backend.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import sep490.com.example.hrms_backend.dto.WorkScheduleRangeDTO;

public class DateRangeValidator implements ConstraintValidator<ValidDateRange, WorkScheduleRangeDTO> {

    @Override
    public boolean isValid(WorkScheduleRangeDTO dto, ConstraintValidatorContext context) {
        if (dto.getStartDate() == null || dto.getEndDate() == null) {
            return true; // đã có @NotNull riêng xử lý
        }

        if (dto.getEndDate().isBefore(dto.getStartDate())) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Ngày kết thúc không được trước ngày bắt đầu")
                    .addPropertyNode("endDate") // highlight ở field endDate
                    .addConstraintViolation();
            return false;
        }

        return true;
    }

}
