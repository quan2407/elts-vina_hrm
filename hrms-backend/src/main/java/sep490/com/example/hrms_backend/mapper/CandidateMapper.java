package sep490.com.example.hrms_backend.mapper;

import sep490.com.example.hrms_backend.dto.CandidateDto;
import sep490.com.example.hrms_backend.entity.Candidate;

import java.util.stream.Collectors;

public class CandidateMapper {
    public static CandidateDto mapToCandidateDto(Candidate candidate, CandidateDto candidateDto) {
        candidateDto.setId(candidate.getId());
        candidateDto.setCandidateName(candidate.getCandidateName());
        candidateDto.setEmail(candidate.getEmail());
        candidateDto.setPhoneNumber(candidate.getPhoneNumber());

        candidateDto.setInterviewScheduleId(
                candidate.getInterviewSchedules().stream()
                        .map(schedule -> schedule.getId())  // đảm bảo có getId()
                        .collect(Collectors.toList())
        );
        return candidateDto;
    }

    public static Candidate mapToCandidate(Candidate candidate, CandidateDto candidateDto) {
        candidate.setId(candidateDto.getId());
        candidate.setCandidateName(candidateDto.getCandidateName());
        candidate.setEmail(candidateDto.getEmail());
        candidate.setPhoneNumber(candidateDto.getPhoneNumber());

        return candidate;
    }
}
