package sep490.com.example.hrms_backend.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import sep490.com.example.hrms_backend.dto.RecruitmentDto;
import sep490.com.example.hrms_backend.service.RecruitmentService;

@RestController
@RequestMapping(path = "/api/recruitment", produces = (MediaType.APPLICATION_JSON_VALUE))
@AllArgsConstructor
@Validated
public class RecruitmentController {

    @Autowired
    private RecruitmentService recruitmentService;


    @GetMapping
    public ResponseEntity<Page<RecruitmentDto>> getRecruitmentList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "createAt") String sortField,
            @RequestParam(defaultValue = "desc") String sortOrder
    ) {
        Page<RecruitmentDto> result =
                recruitmentService.getRecruitmentPage(page, size, search, sortField, sortOrder);
        return ResponseEntity.ok(result);
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
    public ResponseEntity<RecruitmentDto> createRecruitment(@Valid @RequestBody RecruitmentDto recruitmentDto) {
        RecruitmentDto created = recruitmentService.createRecruitment(recruitmentDto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> editRecruitment(@PathVariable Long id, @Valid @RequestBody RecruitmentDto recruitmentDto) {
        try {
            RecruitmentDto updatedDto = recruitmentService.editRecruitment(id, recruitmentDto);
            return new ResponseEntity<>(updatedDto, HttpStatus.OK);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
