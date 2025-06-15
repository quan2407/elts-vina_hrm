package sep490.com.example.hrms_backend.controller;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import sep490.com.example.hrms_backend.dto.CandidateDto;
import sep490.com.example.hrms_backend.service.CandidateService;

@RestController
@RequestMapping(path = "/api/candidate", produces = (MediaType.APPLICATION_JSON_VALUE))
@AllArgsConstructor
@Validated
public class CandidateController {

    @Autowired
    private CandidateService candidateService;

    @PostMapping("/{id}")
    public ResponseEntity<?> addCandidate(@RequestBody CandidateDto candidateDto, @RequestParam Long id) {
        try {

            candidateService.saveCandidate(candidateDto, id);

//            boolean checkCandidate = candidateService.checkCanddateByEmail(candidateDto.getEmail());
//            if(checkCandidate){
//                candidateDto.getRecruitmentId().add(id);
//            candidateService.saveCandidate(candidateDto);
//            return new ResponseEntity<>(candidateDto, HttpStatus.CREATED);
//            }
//            else{
//
//            }
        } catch (Exception e) {
            return new ResponseEntity<>(candidateDto, HttpStatus.BAD_REQUEST);

        }
    }
}
