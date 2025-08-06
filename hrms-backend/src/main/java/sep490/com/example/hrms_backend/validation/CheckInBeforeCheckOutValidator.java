package sep490.com.example.hrms_backend.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import sep490.com.example.hrms_backend.dto.AttendanceCheckInOutDTO;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class CheckInBeforeCheckOutValidator implements ConstraintValidator<ValidCheckInOut, AttendanceCheckInOutDTO> {

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    @Override
    public boolean isValid(AttendanceCheckInOutDTO dto, ConstraintValidatorContext context) {
        if (dto == null) return true;

        String checkIn = dto.getCheckIn();
        String checkOut = dto.getCheckOut();

        if (checkIn == null || checkOut == null || checkIn.isBlank() || checkOut.isBlank()) {
            return true;
        }

        try {
            LocalTime in = LocalTime.parse(checkIn, TIME_FORMATTER);
            LocalTime out = LocalTime.parse(checkOut, TIME_FORMATTER);
            return !in.isAfter(out);
        } catch (Exception e) {
            return true;
        }
    }
}
