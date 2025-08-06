package sep490.com.example.hrms_backend.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = CheckInBeforeCheckOutValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidCheckInOut {
    String message() default "Giờ vào phải nhỏ hơn giờ ra";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
