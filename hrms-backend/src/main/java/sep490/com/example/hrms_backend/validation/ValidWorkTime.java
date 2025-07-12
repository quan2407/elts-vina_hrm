package sep490.com.example.hrms_backend.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = WorkTimeValidator.class)
@Target({ ElementType.TYPE }) // Class-level
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidWorkTime {
    String message() default "Thời gian làm việc không hợp lệ (Giờ bắt đầu phải là 08:00, giờ kết thúc trong khoảng 08:30 - 22:00)";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
