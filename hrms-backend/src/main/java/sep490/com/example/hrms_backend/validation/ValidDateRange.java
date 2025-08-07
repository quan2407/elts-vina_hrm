package sep490.com.example.hrms_backend.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = DateRangeValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidDateRange {
    String message() default "Ngày kết thúc không được nhỏ hơn ngày bắt đầu";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
