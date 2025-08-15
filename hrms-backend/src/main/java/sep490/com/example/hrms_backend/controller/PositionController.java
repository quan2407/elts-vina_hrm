// sep490/com/example/hrms_backend/controller/PositionController.java
package sep490.com.example.hrms_backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import sep490.com.example.hrms_backend.dto.PositionDTO;
import sep490.com.example.hrms_backend.repository.PositionRepository;

import java.util.List;

@RestController
@RequestMapping("/api/positions")
@RequiredArgsConstructor
public class PositionController {
    private final PositionRepository positionRepository;

    @GetMapping
    public List<PositionDTO> getAllPositions() {
        return positionRepository.findAllOrdered()
                .stream().map(PositionDTO::fromEntity).toList();
    }
}
