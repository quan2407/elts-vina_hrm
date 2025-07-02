package sep490.com.example.hrms_backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sep490.com.example.hrms_backend.dto.CandidateDto;
import sep490.com.example.hrms_backend.dto.CandidateResponseDTO;
import sep490.com.example.hrms_backend.entity.Candidate;
import sep490.com.example.hrms_backend.entity.CandidateRecruitment;
import sep490.com.example.hrms_backend.entity.Recruitment;
import sep490.com.example.hrms_backend.enums.CandidateStatus;
import sep490.com.example.hrms_backend.exception.ResourceNotFoundException;
import sep490.com.example.hrms_backend.mapper.CandidateMapper;
import sep490.com.example.hrms_backend.repository.CandidateRecruitmentRepository;
import sep490.com.example.hrms_backend.repository.CandidateRepository;
import sep490.com.example.hrms_backend.repository.RecruitmentRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CandidateService {

    @Autowired
    private CandidateRepository candidateRepository;

    @Autowired
    private RecruitmentRepository recruitmentRepository;

    @Autowired
    private CandidateRecruitmentRepository candidateRecruitmentRepository;

    public boolean checkCandidateByEmail(String email) {
        return candidateRepository.existsByEmail(email);
    }


    public void saveOrUpdateCandidateByPhone(CandidateDto dto, Long recruitmentId) {
        Candidate candidate = candidateRepository.findByPhoneNumber(dto.getPhoneNumber())
                .orElse(null);

        if (candidate == null) {
            // Nếu ứng viên chưa tồn tại → tạo mới
            candidate = CandidateMapper.mapToCandidate(new Candidate(), dto);
        }

        Recruitment recruitment = recruitmentRepository.findById(recruitmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Recruitment not found"));
        // Kiểm tra nếu đã ứng tuyển công việc này
        boolean alreadyApplied = candidate.getCandidateRecruitments().stream()
                .anyMatch(cr -> cr.getRecruitment().getId().equals(recruitmentId));

        if (alreadyApplied) {
            throw new IllegalStateException("Bạn đã ứng tuyển công việc này rồi.");
        }
        // Gắn candidate vào recruitment
        CandidateRecruitment cr = CandidateRecruitment.builder()
                .candidate(candidate)
                .recruitment(recruitment)
                .status(CandidateStatus.APPLIED)
                .submittedAt(LocalDateTime.now())
                .build();

        candidate.getCandidateRecruitments().add(cr);

        // Lưu cả ứng viên và bản ghi liên kết
        candidateRepository.save(candidate);
    }

    public List<CandidateResponseDTO> getCandidatesByRecruitmentId(Long recruitmentId) {
        List<CandidateRecruitment> crList = candidateRecruitmentRepository.findByRecruitmentId(recruitmentId);
        return crList.stream()
                .map(CandidateMapper::toCandidateResponseDTO)
                .toList();
    }


    public void saveCandidate(CandidateDto candidateDto) {
        Candidate candidate = CandidateMapper.mapToCandidate(new Candidate(), candidateDto);
        candidateRepository.save(candidate);
    }

    public void updateCandidateStatus(Long id, String status) {
        CandidateRecruitment candidateRecruitment = candidateRecruitmentRepository.findById(id).orElse(null);
        candidateRecruitment.setStatus(CandidateStatus.valueOf(status));
        candidateRecruitmentRepository.save(candidateRecruitment);
    }
}
