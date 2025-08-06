package sep490.com.example.hrms_backend.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sep490.com.example.hrms_backend.dto.HolidayDTO;
import sep490.com.example.hrms_backend.entity.Holiday;
import sep490.com.example.hrms_backend.mapper.HolidayMapper;
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
                .filter(holiday -> !holiday.isDeleted())
                .map(HolidayMapper::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public HolidayDTO createHoliday(HolidayDTO holidayDTO) {
        Holiday holiday = HolidayMapper.mapToEntity(holidayDTO);
        holiday.setDeleted(false); // đảm bảo không bị xóa
        holiday = holidayRepository.save(holiday);
        return HolidayMapper.mapToDTO(holiday);
    }

    @Override
    public HolidayDTO getHolidayById(Long id) {
        Holiday holiday = holidayRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy ngày nghỉ với ID: " + id));
        return HolidayMapper.mapToDTO(holiday);
    }

    @Override
    public HolidayDTO updateHoliday(Long id, HolidayDTO holidayDTO) {
        Holiday holiday = holidayRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy ngày nghỉ với ID: " + id));
        HolidayMapper.updateHolidayFromDTO(holidayDTO, holiday);
        holiday = holidayRepository.save(holiday);
        return HolidayMapper.mapToDTO(holiday);
    }

    @Override
    public void softDeleteHoliday(Long id) {
        Holiday holiday = holidayRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy ngày nghỉ"));
        holiday.setDeleted(true);
        holidayRepository.save(holiday);
    }

    @Override
    public boolean isHoliday(LocalDate date) {
        boolean isOneTimeHoliday = holidayRepository.existsByStartDateLessThanEqualAndEndDateGreaterThanEqual(date);

        boolean isRecurringHoliday = holidayRepository.findAll().stream()
                .filter(Holiday::isRecurring)
                .filter(h -> !h.isDeleted())
                .anyMatch(holiday -> {
                    LocalDate start = holiday.getStartDate();
                    LocalDate end = holiday.getEndDate();
                    LocalDate targetStart = LocalDate.of(date.getYear(), start.getMonth(), start.getDayOfMonth());
                    LocalDate targetEnd = LocalDate.of(date.getYear(), end.getMonth(), end.getDayOfMonth());
                    return !date.isBefore(targetStart) && !date.isAfter(targetEnd);
                });

        return isOneTimeHoliday || isRecurringHoliday;
    }
}
