package sep490.com.example.hrms_backend.mapper;

import sep490.com.example.hrms_backend.dto.HolidayDTO;
import sep490.com.example.hrms_backend.entity.Holiday;

public class HolidayMapper {

    public static HolidayDTO mapToDTO(Holiday holiday) {
        return new HolidayDTO(
                holiday.getId(),
                holiday.getStartDate(),
                holiday.getEndDate(),
                holiday.getName(),
                holiday.isRecurring()
        );
    }

    public static Holiday mapToEntity(HolidayDTO dto) {
        Holiday holiday = new Holiday();
        holiday.setId(dto.getId());
        holiday.setStartDate(dto.getStartDate());
        holiday.setEndDate(dto.getEndDate());
        holiday.setName(dto.getName());
        holiday.setRecurring(dto.isRecurring());
        holiday.setDeleted(false);
        return holiday;
    }

    public static void updateHolidayFromDTO(HolidayDTO dto, Holiday holiday) {
        holiday.setStartDate(dto.getStartDate());
        holiday.setEndDate(dto.getEndDate());
        holiday.setName(dto.getName());
        holiday.setRecurring(dto.isRecurring());
    }
}
