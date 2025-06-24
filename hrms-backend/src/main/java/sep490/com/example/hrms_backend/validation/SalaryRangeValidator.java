package sep490.com.example.hrms_backend.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import sep490.com.example.hrms_backend.dto.RecruitmentDto;

public class SalaryRangeValidator implements ConstraintValidator<ValidSalaryRange, RecruitmentDto> {

    @Override
    public boolean isValid(RecruitmentDto dto, ConstraintValidatorContext context) {
        if (dto.getMinSalary() == null || dto.getMaxSalary() == null) {
            return true; // Để cho @NotNull lo việc kiểm tra null
        }

        if (dto.getMaxSalary() <= dto.getMinSalary()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Mức lương tối đa phải lớn hơn mức lương tối thiểu")
                    .addPropertyNode("maxSalary")
                    .addConstraintViolation();
            return false;
        }

        return true;
    }
}
