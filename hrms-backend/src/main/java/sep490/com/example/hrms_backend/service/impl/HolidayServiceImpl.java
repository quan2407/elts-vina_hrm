package sep490.com.example.hrms_backend.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import sep490.com.example.hrms_backend.dto.HolidayDTO;
import sep490.com.example.hrms_backend.entity.Holiday;
import sep490.com.example.hrms_backend.exception.HRMSAPIException;
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

    //tested
    @Override
    public List<HolidayDTO> getAllHolidays() {
        return holidayRepository.findAll().stream()
                .filter(holiday -> !holiday.isDeleted())
                .map(HolidayMapper::mapToDTO)
                .collect(Collectors.toList());
    }

    //tested
    @Override
    public HolidayDTO createHoliday(HolidayDTO holidayDTO) {
        validateDates(holidayDTO);
        ensureNoOverlap(holidayDTO, null);
        Holiday holiday = HolidayMapper.mapToEntity(holidayDTO);
        holiday.setDeleted(false);
        holiday = holidayRepository.save(holiday);
        return HolidayMapper.mapToDTO(holiday);
    }

    //tested
    @Override
    public HolidayDTO getHolidayById(Long id) {
        Holiday holiday = holidayRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy ngày nghỉ với ID: " + id));
        return HolidayMapper.mapToDTO(holiday);
    }

    //tested
    @Override
    public HolidayDTO updateHoliday(Long id, HolidayDTO holidayDTO) {
        Holiday holiday = holidayRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy ngày nghỉ với ID: " + id));
        HolidayMapper.updateHolidayFromDTO(holidayDTO, holiday);
        validateDates(holidayDTO);
        ensureNoOverlap(holidayDTO, id);
        holiday = holidayRepository.save(holiday);
        return HolidayMapper.mapToDTO(holiday);
    }

    //tested
    @Override
    public void softDeleteHoliday(Long id) {
        Holiday holiday = holidayRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy ngày nghỉ"));
        holiday.setDeleted(true);
        holidayRepository.save(holiday);
    }

    //tested
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
    private void validateDates(HolidayDTO dto) {
        if (dto.getStartDate() == null || dto.getEndDate() == null) {
            throw new HRMSAPIException(HttpStatus.BAD_REQUEST, "startDate và endDate không được null");
        }
        if (dto.getEndDate().isBefore(dto.getStartDate())) {
            throw new HRMSAPIException(HttpStatus.BAD_REQUEST, "endDate không được nhỏ hơn startDate");
        }
    }

    private boolean rangesOverlap(LocalDate aStart, LocalDate aEnd, LocalDate bStart, LocalDate bEnd) {
        return !aStart.isAfter(bEnd) && !aEnd.isBefore(bStart);
    }

    private void ensureNoOverlap(HolidayDTO dto, Long excludeIdIfUpdate) {
        LocalDate newStart = dto.getStartDate();
        LocalDate newEnd   = dto.getEndDate();
        boolean isRecurringNew = dto.isRecurring();
        boolean oneTimeOverlap;
        if (excludeIdIfUpdate == null) {
            oneTimeOverlap = holidayRepository
                    .existsByIsDeletedFalseAndIsRecurringFalseAndStartDateLessThanEqualAndEndDateGreaterThanEqual(newEnd, newStart);
        } else {
            oneTimeOverlap = holidayRepository
                    .existsByIdNotAndIsDeletedFalseAndIsRecurringFalseAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                            excludeIdIfUpdate, newEnd, newStart
                    );
        }
        if (!isRecurringNew && oneTimeOverlap) {
            throw new HRMSAPIException(HttpStatus.BAD_REQUEST, "Khoảng ngày nghỉ bị trùng với ngày nghỉ khác");
        }


        List<Holiday> recurringList = holidayRepository.findAllByIsDeletedFalseAndIsRecurringTrue();

        List<Holiday> oneTimeList   = holidayRepository.findAllByIsDeletedFalseAndIsRecurringFalse();


        if (excludeIdIfUpdate != null) {
            recurringList = recurringList.stream()
                    .filter(h -> !h.getId().equals(excludeIdIfUpdate))
                    .toList();
            oneTimeList = oneTimeList.stream()
                    .filter(h -> !h.getId().equals(excludeIdIfUpdate))
                    .toList();
        }

        if (isRecurringNew) {
            for (Holiday h : recurringList) {
                LocalDate aStart = LocalDate.of(newStart.getYear(), dto.getStartDate().getMonth(), dto.getStartDate().getDayOfMonth());
                LocalDate aEnd   = LocalDate.of(newStart.getYear(), dto.getEndDate().getMonth(), dto.getEndDate().getDayOfMonth());
                LocalDate bStart = LocalDate.of(newStart.getYear(), h.getStartDate().getMonth(), h.getStartDate().getDayOfMonth());
                LocalDate bEnd   = LocalDate.of(newStart.getYear(), h.getEndDate().getMonth(), h.getEndDate().getDayOfMonth());

                if (rangesOverlap(aStart, aEnd, bStart, bEnd)) {
                    throw new HRMSAPIException(HttpStatus.BAD_REQUEST, "Khoảng ngày nghỉ lặp lại bị trùng với holiday lặp lại khác");
                }
            }
            for (Holiday h : oneTimeList) {
                int year = h.getStartDate().getYear();
                LocalDate aStart = LocalDate.of(year, dto.getStartDate().getMonth(), dto.getStartDate().getDayOfMonth());
                LocalDate aEnd   = LocalDate.of(year, dto.getEndDate().getMonth(), dto.getEndDate().getDayOfMonth());
                if (rangesOverlap(aStart, aEnd, h.getStartDate(), h.getEndDate())) {
                    throw new HRMSAPIException(HttpStatus.BAD_REQUEST, "Khoảng ngày nghỉ lặp lại bị trùng với holiday one-time");
                }
            }
        } else {
            int year = newStart.getYear();
            for (Holiday h : recurringList) {
                LocalDate bStart = LocalDate.of(year, h.getStartDate().getMonth(), h.getStartDate().getDayOfMonth());
                LocalDate bEnd   = LocalDate.of(year, h.getEndDate().getMonth(), h.getEndDate().getDayOfMonth());
                if (rangesOverlap(newStart, newEnd, bStart, bEnd)) {
                    throw new HRMSAPIException(HttpStatus.BAD_REQUEST, "Khoảng ngày nghỉ bị trùng với holiday lặp lại");
                }
            }
        }
    }

}
