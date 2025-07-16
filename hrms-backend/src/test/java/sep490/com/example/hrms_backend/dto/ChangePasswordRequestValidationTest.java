package sep490.com.example.hrms_backend.dto;

import jakarta.validation.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ChangePasswordRequestValidationTest {

    private Validator validator;

    @BeforeEach
    void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void newPasswordTooShort_shouldFailValidation() {
        ChangePasswordRequest req = new ChangePasswordRequest("old", "aA1", "aA1");

        Set<ConstraintViolation<ChangePasswordRequest>> violations = validator.validate(req);
        assertFalse(violations.isEmpty());
    }

    @Test
    void newPasswordInvalidPattern_shouldFailValidation() {
        ChangePasswordRequest req = new ChangePasswordRequest("old", "password", "password");

        Set<ConstraintViolation<ChangePasswordRequest>> violations = validator.validate(req);
        assertFalse(violations.isEmpty());
    }

    @Test
    void confirmPasswordBlank_shouldFailValidation() {
        ChangePasswordRequest req = new ChangePasswordRequest("old", "Valid123A", "");

        Set<ConstraintViolation<ChangePasswordRequest>> violations = validator.validate(req);
        assertFalse(violations.isEmpty());
    }

    @Test
    void allFieldsValid_shouldPassValidation() {
        ChangePasswordRequest req = new ChangePasswordRequest("oldPass", "Newpass123", "Newpass123");

        Set<ConstraintViolation<ChangePasswordRequest>> violations = validator.validate(req);
        assertTrue(violations.isEmpty());
    }
    @Test
    void oldPasswordBlank_shouldFailValidation() {
        ChangePasswordRequest req = new ChangePasswordRequest(
                "", "Newpass123", "Newpass123"
        );

        Set<ConstraintViolation<ChangePasswordRequest>> violations = validator.validate(req);
        assertFalse(violations.isEmpty());
    }

}
