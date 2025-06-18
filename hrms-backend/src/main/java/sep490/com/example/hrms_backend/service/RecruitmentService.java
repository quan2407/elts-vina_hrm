package sep490.com.example.hrms_backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sep490.com.example.hrms_backend.dto.RecruitmentDto;
import sep490.com.example.hrms_backend.entity.Recruitment;
import sep490.com.example.hrms_backend.mapper.RecruitmentMapper;
import sep490.com.example.hrms_backend.repository.RecruitmentRepository;

import java.util.List;

@Service
public class RecruitmentService {

    @Autowired
    private RecruitmentRepository recruitmentRepository;



    public List<RecruitmentDto> getRecruitmentList() {

        return RecruitmentMapper.mapToRecruitmentDtoList(recruitmentRepository.findAll()) ;
    }

    public RecruitmentDto getRecruitmentDtoById(long id){
        Recruitment recruitment = recruitmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Recruitment not found with id: " + id));
        return RecruitmentMapper.mapToRecruitmentDto(recruitment, new RecruitmentDto());
    }

}
