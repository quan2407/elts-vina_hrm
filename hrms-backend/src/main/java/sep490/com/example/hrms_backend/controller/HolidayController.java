package sep490.com.example.hrms_backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sep490.com.example.hrms_backend.dto.HolidayDTO;
import sep490.com.example.hrms_backend.service.HolidayService;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/holidays")
@RequiredArgsConstructor
public class HolidayController {

    private final HolidayService holidayService;

    @GetMapping
    public ResponseEntity<List<HolidayDTO>> getAllHolidays() {
        List<HolidayDTO> holidays = holidayService.getAllHolidays();
        return ResponseEntity.ok(holidays);
    }

    @PostMapping
    public ResponseEntity<HolidayDTO> createHoliday(@RequestBody HolidayDTO holidayDTO) {
        HolidayDTO createdHoliday = holidayService.createHoliday(holidayDTO);
        return ResponseEntity.ok(createdHoliday);
    }

    @GetMapping("/check/{date}")
    public ResponseEntity<Boolean> checkIfHoliday(@PathVariable String date) {
        LocalDate localDate = LocalDate.parse(date);
        boolean isHoliday = holidayService.isHoliday(localDate);
        return ResponseEntity.ok(isHoliday);
    }
}
