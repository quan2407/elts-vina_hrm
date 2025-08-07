package sep490.com.example.hrms_backend.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = WorkDateRangeValidator.class)
@Target({ ElementType.TYPE }) // Áp dụng cho class
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidWorkDateRange {
    String message() default "Ngày nghỉ phải sau ngày vào công ty";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
