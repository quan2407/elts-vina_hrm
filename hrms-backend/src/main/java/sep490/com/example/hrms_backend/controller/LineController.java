package sep490.com.example.hrms_backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import sep490.com.example.hrms_backend.dto.DepartmentDTO;
import sep490.com.example.hrms_backend.service.LineService;

@RestController
@RequestMapping("/api/lines")
@RequiredArgsConstructor
public class LineController {

    private final LineService lineService;

    @GetMapping("/{lineId}/department")
    public DepartmentDTO getDepartmentByLineId(@PathVariable Long lineId) {
        return lineService.getDepartmentByLineId(lineId);
    }
}
