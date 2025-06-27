package sep490.com.example.hrms_backend.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;
import java.time.Period;

public class Age18Validator implements ConstraintValidator<Age18, LocalDate> {

    @Override
    public void initialize(Age18 constraintAnnotation) {
    }

    @Override
    public boolean isValid(LocalDate dob, ConstraintValidatorContext context) {
        if (dob == null) {
            return true;  // Return false if date of birth is null
        }

        // Tính tuổi
        LocalDate today = LocalDate.now();
        int age = Period.between(dob, today).getYears();

        // Kiểm tra nếu tuổi nhỏ hơn 18
        return age >= 18;  // Return true if age is 18 or older
    }
}
