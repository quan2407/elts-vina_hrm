package sep490.com.example.hrms_backend.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sep490.com.example.hrms_backend.dto.HolidayDTO;
import sep490.com.example.hrms_backend.entity.Holiday;
import sep490.com.example.hrms_backend.repository.HolidayRepository;
import sep490.com.example.hrms_backend.service.HolidayService;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HolidayServiceImpl implements HolidayService {

    private final HolidayRepository holidayRepository;

    @Override
    public List<HolidayDTO> getAllHolidays() {
        return holidayRepository.findAll().stream()
                .map(holiday -> new HolidayDTO(holiday.getId(), holiday.getStartDate(), holiday.getEndDate(), holiday.getName(), holiday.isRecurring()))
                .collect(Collectors.toList());
    }

    @Override
    public HolidayDTO createHoliday(HolidayDTO holidayDTO) {
        Holiday holiday = new Holiday();
        holiday.setStartDate(holidayDTO.getStartDate());
        holiday.setEndDate(holidayDTO.getEndDate());
        holiday.setName(holidayDTO.getName());
        holiday.setRecurring(holidayDTO.isRecurring());

        holiday = holidayRepository.save(holiday);

        return new HolidayDTO(holiday.getId(), holiday.getStartDate(), holiday.getEndDate(), holiday.getName(), holiday.isRecurring());
    }

    @Override
    public boolean isHoliday(LocalDate date) {
        return holidayRepository.existsByStartDateLessThanEqualAndEndDateGreaterThanEqual(date);
    }
}
