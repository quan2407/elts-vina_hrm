package sep490.com.example.hrms_backend.service;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import sep490.com.example.hrms_backend.dto.RecruitmentDto;
import sep490.com.example.hrms_backend.entity.Account;
import sep490.com.example.hrms_backend.entity.Department;
import sep490.com.example.hrms_backend.entity.Recruitment;
import sep490.com.example.hrms_backend.enums.RecruitmentStatus;
import sep490.com.example.hrms_backend.mapper.RecruitmentMapper;
import sep490.com.example.hrms_backend.repository.AccountRepository;
import sep490.com.example.hrms_backend.repository.DepartmentRepository;
import sep490.com.example.hrms_backend.repository.RecruitmentRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RecruitmentService {

    @Autowired
    private RecruitmentRepository recruitmentRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private AccountRepository accountRepository;

    public List<RecruitmentDto> getRecruitmentList() {

        return RecruitmentMapper.mapToRecruitmentDtoList(recruitmentRepository.findAll());
    }

    public RecruitmentDto getRecruitmentDtoById(long id) {
        Recruitment recruitment = recruitmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Recruitment not found with id: " + id));
        return RecruitmentMapper.mapToRecruitmentDto(recruitment, new RecruitmentDto());
    }

    public RecruitmentDto createRecruitment(@Valid RecruitmentDto recruitmentDto) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        Recruitment recruitment = new Recruitment();
        recruitment = RecruitmentMapper.mapToRecruitment(recruitment, recruitmentDto);
        Department department = departmentRepository.findById(recruitmentDto.getDepartmentId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phòng ban"));
        recruitment.setDepartment(department);
        recruitment.setCreateAt(LocalDateTime.now());
        recruitment.setUpdateAt(LocalDateTime.now());
        recruitment.setStatus(RecruitmentStatus.OPEN);
        Account account = accountRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Account not found"));
        recruitment.setCreatedBy(account.getEmployee());
        Recruitment saved = recruitmentRepository.save(recruitment);

        return RecruitmentMapper.mapToRecruitmentDto(saved, new RecruitmentDto());
    }
}
