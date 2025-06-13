    package sep490.com.example.hrms_backend.controller;

    import lombok.AllArgsConstructor;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.http.HttpStatus;
    import org.springframework.http.MediaType;
    import org.springframework.http.ResponseEntity;
    import org.springframework.validation.annotation.Validated;
    import org.springframework.web.bind.annotation.GetMapping;
    import org.springframework.web.bind.annotation.PathVariable;
    import org.springframework.web.bind.annotation.RequestMapping;
    import org.springframework.web.bind.annotation.RestController;
    import sep490.com.example.hrms_backend.dto.RecruitmentDto;
    import sep490.com.example.hrms_backend.entity.Recruitment;
    import sep490.com.example.hrms_backend.mapper.RecuitmentMapper;
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
    }
