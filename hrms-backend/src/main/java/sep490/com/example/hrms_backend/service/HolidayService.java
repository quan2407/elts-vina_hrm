package sep490.com.example.hrms_backend.service;

import sep490.com.example.hrms_backend.dto.HolidayDTO;

import java.time.LocalDate;
import java.util.List;

public interface HolidayService {
    List<HolidayDTO> getAllHolidays();
    HolidayDTO createHoliday(HolidayDTO holidayDTO);
    boolean isHoliday(LocalDate date);

    void softDeleteHoliday(Long id);

    HolidayDTO getHolidayById(Long id);
    HolidayDTO updateHoliday(Long id, HolidayDTO holidayDTO);
}
