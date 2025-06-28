package sep490.com.example.hrms_backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import sep490.com.example.hrms_backend.entity.CandidateRecruitment;
import sep490.com.example.hrms_backend.repository.CandidateRecruitmentRepository;

import java.util.Optional;

public class CandidateRecruitmentService {

    @Autowired
    CandidateRecruitmentRepository candidateRecruitmentRepository;

    public Optional<CandidateRecruitment> findById(Long candidateRecruitmentId) {

        return candidateRecruitmentRepository.findById(candidateRecruitmentId);

    }
}
