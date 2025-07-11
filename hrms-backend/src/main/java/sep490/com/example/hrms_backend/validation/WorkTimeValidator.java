package sep490.com.example.hrms_backend.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import sep490.com.example.hrms_backend.dto.WorkScheduleDetailCreateDTO;
import sep490.com.example.hrms_backend.dto.WorkScheduleDetailUpdateDTO;

import java.time.LocalTime;

public class WorkTimeValidator implements ConstraintValidator<ValidWorkTime, Object> {

    private static final LocalTime START_TIME = LocalTime.of(8, 0);
    private static final LocalTime MIN_END_TIME = LocalTime.of(8, 30);
    private static final LocalTime MAX_END_TIME = LocalTime.of(22, 0);

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        LocalTime start = null;
        LocalTime end = null;

        if (value instanceof WorkScheduleDetailCreateDTO dto) {
            start = dto.getStartTime();
            end = dto.getEndTime();
        } else if (value instanceof WorkScheduleDetailUpdateDTO dto) {
            start = dto.getStartTime();
            end = dto.getEndTime();
        }

        if (start == null || end == null) {
            return false;
        }

        boolean isStartValid = start.equals(START_TIME);
        boolean isEndValid = !end.isBefore(MIN_END_TIME) && !end.isAfter(MAX_END_TIME);

        if (!isStartValid || !isEndValid) {
            context.disableDefaultConstraintViolation();

            if (!isStartValid) {
                context.buildConstraintViolationWithTemplate("Giờ bắt đầu phải là 08:00")
                        .addPropertyNode("startTime")
                        .addConstraintViolation();
            }

            if (!isEndValid) {
                context.buildConstraintViolationWithTemplate("Giờ kết thúc phải trong khoảng 08:30 đến 22:00")
                        .addPropertyNode("endTime")
                        .addConstraintViolation();
            }

            return false;
        }

        return true;
    }


}
