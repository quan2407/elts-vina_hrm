package sep490.com.example.hrms_backend.validation;

import org.springframework.stereotype.Component;
import sep490.com.example.hrms_backend.dto.ApplicationCreateDTO;
import sep490.com.example.hrms_backend.exception.HRMSFieldValidationException;

import java.time.LocalTime;
import java.util.*;

@Component
public class ApplicationValidator {

    public void validate(ApplicationCreateDTO dto, String applicationTypeName) {
        Map<String, List<String>> errors = new HashMap<>();

        if (dto.getTitle() == null || dto.getTitle().trim().isEmpty()) {
            errors.computeIfAbsent("title", k -> new ArrayList<>()).add("Tiêu đề không được để trống");
        }

        if (dto.getContent() == null || dto.getContent().trim().isEmpty()) {
            errors.computeIfAbsent("content", k -> new ArrayList<>()).add("Nội dung không được để trống");
        }

        if (dto.getStartDate() == null) {
            errors.computeIfAbsent("startDate", k -> new ArrayList<>()).add("Phải chọn ngày bắt đầu");
        }

        if (dto.getEndDate() == null) {
            errors.computeIfAbsent("endDate", k -> new ArrayList<>()).add("Phải chọn ngày kết thúc");
        }

        if (dto.getStartDate() != null && dto.getEndDate() != null
                && dto.getEndDate().isBefore(dto.getStartDate())) {
            errors.computeIfAbsent("endDate", k -> new ArrayList<>()).add("Ngày kết thúc không được nhỏ hơn ngày bắt đầu");
        }

        if ("Nghỉ phép".equalsIgnoreCase(applicationTypeName)) {
            if (dto.getLeaveCode() == null) {
                errors.computeIfAbsent("leaveCode", k -> new ArrayList<>()).add("Vui lòng chọn loại nghỉ");
            }
        }

        if ("Bù công".equalsIgnoreCase(applicationTypeName)) {
            LocalTime checkIn = dto.getCheckIn();
            LocalTime checkOut = dto.getCheckOut();

            if (checkIn == null) {
                errors.computeIfAbsent("checkIn", k -> new ArrayList<>()).add("Vui lòng nhập giờ vào");
            }
            if (checkOut == null) {
                errors.computeIfAbsent("checkOut", k -> new ArrayList<>()).add("Vui lòng nhập giờ ra");
            }

            if (checkIn != null && checkOut != null &&
                    (checkOut.isBefore(checkIn) || checkOut.equals(checkIn))) {
                errors.computeIfAbsent("checkOut", k -> new ArrayList<>()).add("Giờ ra phải lớn hơn giờ vào");
            }
        }

        if (!errors.isEmpty()) {
            throw new HRMSFieldValidationException(errors);
        }
    }
}
