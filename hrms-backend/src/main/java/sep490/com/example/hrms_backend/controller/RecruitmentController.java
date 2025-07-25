package sep490.com.example.hrms_backend.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import sep490.com.example.hrms_backend.dto.RecruitmentDto;
import sep490.com.example.hrms_backend.service.RecruitmentService;

import java.util.List;

@RestController
@RequestMapping(path = "/api/recruitment", produces = (MediaType.APPLICATION_JSON_VALUE))
@AllArgsConstructor
@Validated
public class RecruitmentController {

    @Autowired
    private RecruitmentService recruitmentService;


    @GetMapping()
    public ResponseEntity<?> getRecruitmentList(@RequestParam(required = false) String search,
                                                @RequestParam(required = false, defaultValue = "createAt") String sortField,
                                                @RequestParam(required = false, defaultValue = "desc") String sortOrder) {
        List<RecruitmentDto> recruitmentDtoList = recruitmentService.getRecruitmentList(search, sortField, sortOrder);
        if (recruitmentDtoList != null && !recruitmentDtoList.isEmpty()) {
            return new ResponseEntity<>(recruitmentDtoList, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getRecruitmentById(@PathVariable Long id) {

        RecruitmentDto recruitmentDto = recruitmentService.getRecruitmentDtoById(id);
        if (recruitmentDto != null) {
            return new ResponseEntity<>(recruitmentDto, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('HR', 'HR_MANAGER')")
    public ResponseEntity<RecruitmentDto> createRecruitment(@Valid @RequestBody RecruitmentDto recruitmentDto) {
        RecruitmentDto created = recruitmentService.createRecruitment(recruitmentDto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('HR', 'HR_MANAGER')")
    public ResponseEntity<?> editRecruitment(@PathVariable Long id, @Valid @RequestBody RecruitmentDto recruitmentDto) {
        try {
            RecruitmentDto updatedDto = recruitmentService.editRecruitment(id, recruitmentDto);
            return new ResponseEntity<>(updatedDto, HttpStatus.OK);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
