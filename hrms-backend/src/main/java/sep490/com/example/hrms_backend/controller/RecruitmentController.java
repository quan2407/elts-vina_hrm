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
        public ResponseEntity<?> getRecruitmentList() {
            List<RecruitmentDto> recruitmentDtoList = recruitmentService.getRecruitmentList() ;
            if(recruitmentDtoList != null && !recruitmentDtoList.isEmpty()){
                return new ResponseEntity<>(recruitmentDtoList, HttpStatus.OK);
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);    }

        @GetMapping("/{id}")
        public ResponseEntity<?> getRecruitmentById(@PathVariable Long id) {

            RecruitmentDto recruitmentDto= recruitmentService.getRecruitmentDtoById(id);
            if(recruitmentDto != null){
            return new ResponseEntity<>(recruitmentDto, HttpStatus.OK);}
            return  new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        @PostMapping
        @PreAuthorize("hasAnyRole('HR')")
        public ResponseEntity<RecruitmentDto> createRecruitment(@Valid @RequestBody RecruitmentDto recruitmentDto) {
            RecruitmentDto created = recruitmentService.createRecruitment(recruitmentDto);
            return new ResponseEntity<>(created, HttpStatus.CREATED);
        }
    }
