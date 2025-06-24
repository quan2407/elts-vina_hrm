package sep490.com.example.hrms_backend.mapper;

import sep490.com.example.hrms_backend.dto.CandidateDto;
import sep490.com.example.hrms_backend.dto.CandidateResponseDTO;
import sep490.com.example.hrms_backend.entity.Candidate;
import sep490.com.example.hrms_backend.entity.CandidateRecruitment;

import java.util.stream.Collectors;

public class CandidateMapper {
    public static CandidateDto mapToCandidateDto(Candidate candidate, CandidateDto candidateDto) {
        candidateDto.setId(candidate.getId());
        candidateDto.setCandidateName(candidate.getCandidateName());
        candidateDto.setGender(candidateDto.getGender());
        candidateDto.setDob(candidate.getDob());
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
        if (candidate == null) {
            candidate = new Candidate();
        }

        candidate.setId(candidateDto.getId());
        candidate.setCandidateName(candidateDto.getCandidateName());
        candidate.setGender(candidateDto.getGender());
        candidate.setDob(candidateDto.getDob());
        candidate.setEmail(candidateDto.getEmail());
        candidate.setPhoneNumber(candidateDto.getPhoneNumber());

        // Nếu DTO có thêm các trường như `recruitmentId`, `interviewScheduleId`
        // thì nên xử lý ở tầng SERVICE, không xử lý ánh xạ tại MAPPER

        return candidate;
    }

    public static CandidateResponseDTO toCandidateResponseDTO(CandidateRecruitment cr) {
        CandidateResponseDTO dto = new CandidateResponseDTO();

        Candidate candidate = cr.getCandidate();

        dto.setId(candidate.getId());
        dto.setCandidateName(candidate.getCandidateName());
        dto.setGender(candidate.getGender());
        dto.setDob(candidate.getDob());
        dto.setEmail(candidate.getEmail());
        dto.setPhoneNumber(candidate.getPhoneNumber());

        dto.setStatus(cr.getStatus());
        dto.setSubmittedAt(cr.getSubmittedAt());
        dto.setNote(cr.getNote());
        dto.setCandidateRecruitmentId(cr.getId());
        if (cr.getRecruitment() != null) {
            dto.setRecruitmentId(cr.getRecruitment().getId());
        }

        return dto;
    }

}
