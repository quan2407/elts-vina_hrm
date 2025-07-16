package sep490.com.example.hrms_backend.dto;

import jakarta.validation.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sep490.com.example.hrms_backend.enums.Gender;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class EmployeeRequestDTOValidationTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    private EmployeeRequestDTO.EmployeeRequestDTOBuilder buildValidDTO() {
        return EmployeeRequestDTO.builder()
                .employeeCode("ELTSSX0001")
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
        EmployeeRequestDTO dto = buildValidDTO().build();
        Set<ConstraintViolation<EmployeeRequestDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    void employeeCode_invalidFormat_shouldFail() {
        EmployeeRequestDTO dto = buildValidDTO().employeeCode("ABC123").build();
        Set<ConstraintViolation<EmployeeRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("employeeCode")));
    }

    @Test
    void dob_under18_shouldFail() {
        LocalDate dob = LocalDate.now().minusYears(17);
        EmployeeRequestDTO dto = buildValidDTO().dob(dob).build();

        Set<ConstraintViolation<EmployeeRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("dob")));
    }

    @Test
    void email_invalidFormat_shouldFail() {
        EmployeeRequestDTO dto = buildValidDTO().email("not-an-email").build();
        Set<ConstraintViolation<EmployeeRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("email")));
    }

    @Test
    void phoneNumber_invalid_shouldFail() {
        EmployeeRequestDTO dto = buildValidDTO().phoneNumber("123abc!").build();
        Set<ConstraintViolation<EmployeeRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("phoneNumber")));
    }

    @Test
    void citizenExpiryDate_inPast_shouldFail() {
        EmployeeRequestDTO dto = buildValidDTO().citizenExpiryDate(LocalDate.of(2000, 1, 1)).build();
        Set<ConstraintViolation<EmployeeRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("citizenExpiryDate")));
    }

    @Test
    void trainingMajor_blank_shouldFail() {
        EmployeeRequestDTO dto = buildValidDTO().trainingMajor(" ").build();
        Set<ConstraintViolation<EmployeeRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("trainingMajor")));
    }
    @Test
    void employeeName_containsNumber_shouldFail() {
        EmployeeRequestDTO dto = buildValidDTO().employeeName("Nguyễn Văn 9").build();
        Set<ConstraintViolation<EmployeeRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("employeeName")));
    }
    @Test
    void citizenId_invalidFormat_shouldFail() {
        EmployeeRequestDTO dto = buildValidDTO().citizenId("abc123").build();
        Set<ConstraintViolation<EmployeeRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("citizenId")));
    }
    @Test
    void departmentId_null_shouldFail() {
        EmployeeRequestDTO dto = buildValidDTO().departmentId(null).build();
        Set<ConstraintViolation<EmployeeRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("departmentId")));
    }

}
