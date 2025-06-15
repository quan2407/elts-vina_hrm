package sep490.com.example.hrms_backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sep490.com.example.hrms_backend.dto.CandidateDto;
import sep490.com.example.hrms_backend.entity.Candidate;
import sep490.com.example.hrms_backend.mapper.CandidateMapper;
import sep490.com.example.hrms_backend.repository.CandidateRepository;

@Service
public class CandidateService {

    @Autowired
    private CandidateRepository candidateRepository;

    public boolean checkCandidateByEmail(String email) {
        return candidateRepository.existsByEmail(email);
    }

    public CandidateDto getCandidateByEmail(String email) {

        Candidate candidate = candidateRepository.findByEmail(email);
        CandidateDto candidateDto = null;
        return CandidateMapper.mapToCandidateDto(candidate, candidateDto);
    }

    public void saveCandidate(CandidateDto candidateDto, Long id) {
        boolean checkCandidate = this.checkCandidateByEmail(candidateDto.getEmail());

        if (!checkCandidate) {
            saveCandidate(candidateDto);
            Candidate candidate
        }

    }


    public void saveCandidate(CandidateDto candidateDto) {
        Candidate candidate = CandidateMapper.mapToCandidate(new Candidate(), candidateDto);
        candidateRepository.save(candidate);
    }
}
