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
import org.springframework.scheduling.annotation.Scheduled;

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

    public RecruitmentDto editRecruitment(Long id, @Valid RecruitmentDto recruitmentDto) {

        Recruitment recruitment = recruitmentRepository.findById(id).orElseThrow(() -> new RuntimeException("Không tìm thấy Recruitment với ID: " + id));

        recruitment.setTitle(recruitmentDto.getTitle());
        recruitment.setWorkLocation(recruitmentDto.getWorkLocation());
        recruitment.setEmploymentType(recruitmentDto.getEmploymentType());
        recruitment.setJobDescription(recruitmentDto.getJobDescription());
        recruitment.setJobRequirement(recruitmentDto.getJobRequirement());
        recruitment.setBenefits(recruitmentDto.getBenefits());
        recruitment.setMinSalary(recruitmentDto.getMinSalary());
        recruitment.setMaxSalary(recruitmentDto.getMaxSalary());
        recruitment.setQuantity(recruitmentDto.getQuantity());
        recruitment.setExpiredAt(recruitmentDto.getExpiredAt());
        recruitment.setUpdateAt(LocalDateTime.now());
        recruitment.setStatus(RecruitmentStatus.OPEN);
        // Cập nhật phòng ban nếu thay đổi
        if (recruitmentDto.getDepartmentId() != null) {
            Department department = departmentRepository.findById(recruitmentDto.getDepartmentId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy phòng ban với ID: " + recruitmentDto.getDepartmentId()));
            recruitment.setDepartment(department);
        }
        Recruitment updated = recruitmentRepository.save(recruitment);
        return RecruitmentMapper.mapToRecruitmentDto(updated, new RecruitmentDto());
    }

    @Scheduled(cron = "0 0 0 * * ?")
    public void updateRecruitmentStatus() {
        List<Recruitment> recruitments = recruitmentRepository.findAll();

        for (Recruitment recruitment : recruitments) {
            if (recruitment.getExpiredAt() != null && recruitment.getExpiredAt().isBefore(LocalDateTime.now())) {
                if (recruitment.getStatus() != RecruitmentStatus.CLOSE) {
                    recruitment.setStatus(RecruitmentStatus.CLOSE);
                    recruitment.setUpdateAt(LocalDateTime.now());
                    recruitmentRepository.save(recruitment); // Cập nhật trạng thái mới
                }
            }
        }
    }
}

