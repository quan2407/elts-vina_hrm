package sep490.com.example.hrms_backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sep490.com.example.hrms_backend.dto.DepartmentDTO;
import sep490.com.example.hrms_backend.dto.LineDTO;
import sep490.com.example.hrms_backend.dto.LinePMCDto;
import sep490.com.example.hrms_backend.service.LineService;
import sep490.com.example.hrms_backend.utils.CurrentUserUtils;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/lines")
@RequiredArgsConstructor
public class LineController {

    @Autowired
    private LineService lineService;

    final  private CurrentUserUtils currentUserUtils;

    @GetMapping("/{lineId}/department")
    public DepartmentDTO getDepartmentByLineId(@PathVariable Long lineId) {
        return lineService.getDepartmentByLineId(lineId);
    }

    @GetMapping
    public ResponseEntity<?> getLine(@RequestParam(required = false) String search) {
        List<LinePMCDto> linePMCDtoList = lineService.getAllLine(search);
        return new ResponseEntity<>(linePMCDtoList, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public LineDTO getLineByLineId(@PathVariable Long id) {
        return lineService.getLineByLineId(id);
    }

    @PutMapping("/{lineId}/leader")
    public ResponseEntity<?> updateLineLeader(
            @PathVariable Long lineId,
            @RequestBody Map<String, Long> requestBody) {
        Long leaderId = requestBody.get("leaderId");
        Long senderId = currentUserUtils.getCurrentEmployeeId();
        try {
            lineService.assignLeaderToLine(lineId, leaderId, senderId);
            return ResponseEntity.ok("Cập nhật tổ trưởng thành công.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi: " + e.getMessage());
        }
    }


}
