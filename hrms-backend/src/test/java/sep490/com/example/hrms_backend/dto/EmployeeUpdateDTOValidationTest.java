package sep490.com.example.hrms_backend.dto;

import jakarta.validation.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sep490.com.example.hrms_backend.enums.Gender;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class EmployeeUpdateDTOValidationTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    private EmployeeUpdateDTO.EmployeeUpdateDTOBuilder buildValidDTO() {
        return EmployeeUpdateDTO.builder()
                .employeeName("Nguyễn Văn A")
                .gender(Gender.NAM)
                .dob(LocalDate.of(1995, 1, 1))
                .placeOfBirth("Hà Nội")
                .originPlace("Nam Định")
                .nationality("Việt Nam")
                .citizenId("012345678901")
                .citizenIssueDate(LocalDate.of(2015, 1, 1))
                .citizenExpiryDate(LocalDate.now().plusYears(5))
                .address("123 đường ABC")
                .currentAddress("Hà Nội")
                .ethnicity("Kinh")
                .religion("Không")
                .educationLevel("12/12")
                .specializedLevel("Kỹ sư")
                .foreignLanguages("Tiếng Anh")
                .trainingType("Chính quy")
                .trainingMajor("CNTT")
                .startWorkAt(LocalDate.now())
                .phoneNumber("0987654321")
                .email("test@example.com")
                .departmentId(1L)
                .positionId(1L);
    }

    @Test
    void validDTO_shouldPassValidation() {
        EmployeeUpdateDTO dto = buildValidDTO().build();
        Set<ConstraintViolation<EmployeeUpdateDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    void employeeName_containsNumber_shouldFail() {
        EmployeeUpdateDTO dto = buildValidDTO().employeeName("Nguyễn Văn 9").build();
        Set<ConstraintViolation<EmployeeUpdateDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
    }

    @Test
    void citizenId_invalid_shouldFail() {
        EmployeeUpdateDTO dto = buildValidDTO().citizenId("abc123").build();
        Set<ConstraintViolation<EmployeeUpdateDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
    }

    @Test
    void email_invalidFormat_shouldFail() {
        EmployeeUpdateDTO dto = buildValidDTO().email("not-an-email").build();
        Set<ConstraintViolation<EmployeeUpdateDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
    }

    @Test
    void phoneNumber_invalid_shouldFail() {
        EmployeeUpdateDTO dto = buildValidDTO().phoneNumber("123abc!").build();
        Set<ConstraintViolation<EmployeeUpdateDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
    }

    @Test
    void dob_inFuture_shouldFail() {
        EmployeeUpdateDTO dto = buildValidDTO().dob(LocalDate.now().plusDays(1)).build();
        Set<ConstraintViolation<EmployeeUpdateDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
    }

    @Test
    void citizenIssueDate_inFuture_shouldFail() {
        EmployeeUpdateDTO dto = buildValidDTO().citizenIssueDate(LocalDate.now().plusDays(1)).build();
        Set<ConstraintViolation<EmployeeUpdateDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
    }

    @Test
    void citizenExpiryDate_inPast_shouldFail() {
        EmployeeUpdateDTO dto = buildValidDTO().citizenExpiryDate(LocalDate.of(2000, 1, 1)).build();
        Set<ConstraintViolation<EmployeeUpdateDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
    }

    @Test
    void startWorkAt_inFuture_shouldFail() {
        EmployeeUpdateDTO dto = buildValidDTO().startWorkAt(LocalDate.now().plusDays(10)).build();
        Set<ConstraintViolation<EmployeeUpdateDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
    }

    @Test
    void departmentId_null_shouldFail() {
        EmployeeUpdateDTO dto = buildValidDTO().departmentId(null).build();
        Set<ConstraintViolation<EmployeeUpdateDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
    }

    @Test
    void trainingMajor_blank_shouldFail() {
        EmployeeUpdateDTO dto = buildValidDTO().trainingMajor("   ").build();
        Set<ConstraintViolation<EmployeeUpdateDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
    }
}
