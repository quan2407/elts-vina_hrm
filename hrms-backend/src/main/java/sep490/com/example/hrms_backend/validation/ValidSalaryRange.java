package sep490.com.example.hrms_backend.validation;


import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = SalaryRangeValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidSalaryRange {
    String message() default "Lương tối đa phải lớn hơn lương tối thiểu";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
