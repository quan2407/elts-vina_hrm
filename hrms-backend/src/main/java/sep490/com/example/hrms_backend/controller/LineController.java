package sep490.com.example.hrms_backend.controller;

import lombok.RequiredArgsConstructor;
import org.apache.catalina.LifecycleState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sep490.com.example.hrms_backend.dto.DepartmentDTO;
import sep490.com.example.hrms_backend.dto.LinePMCDto;
import sep490.com.example.hrms_backend.service.LineService;

import java.util.List;

@RestController
@RequestMapping("/api/lines")
@RequiredArgsConstructor
public class LineController {

    @Autowired
    private LineService lineService;

    @GetMapping("/{lineId}/department")
    public DepartmentDTO getDepartmentByLineId(@PathVariable Long lineId) {
        return lineService.getDepartmentByLineId(lineId);
    }

    @GetMapping
    public ResponseEntity<?> getLine(@RequestParam(required = false) String search){
        List<LinePMCDto> linePMCDtoList = lineService.getAllLine(search);
            return new ResponseEntity<>(linePMCDtoList,HttpStatus.OK);
    }
}
