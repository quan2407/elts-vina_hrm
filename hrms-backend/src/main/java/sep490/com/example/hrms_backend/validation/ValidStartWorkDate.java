package sep490.com.example.hrms_backend.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = StartWorkDateValidator.class)
@Target({ ElementType.TYPE }) // Áp dụng cho class
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidStartWorkDate {
    String message() default "Ngày vào công ty phải sau ngày sinh ít nhất 18 năm";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
