package sep490.com.example.hrms_backend.mapper;

import sep490.com.example.hrms_backend.dto.RecruitmentDto;
import sep490.com.example.hrms_backend.entity.Recruitment;

public class RecuitmentMapper {
    public static RecruitmentDto mapToRecruitmentDto(Recruitment recruitment, RecruitmentDto recruitmentDto) {
        recruitmentDto.setTitle(recruitment.getTitle());
        recruitmentDto.setWorkLocation(recruitment.getWorkLocation());
        recruitmentDto.setEmploymentType(recruitment.getEmploymentType());
        recruitmentDto.setJobDescription(recruitment.getJobDescription());
        recruitmentDto.setJobRequirement(recruitment.getJobRequirement());
        recruitmentDto.setBenefits(recruitment.getBenefits());
        recruitmentDto.setSalaryRange(recruitment.getSalaryRange());
        recruitmentDto.setQuantity(recruitment.getQuantity());
        recruitmentDto.setExpiredAt(recruitment.getExpiredAt());
        recruitmentDto.setCreateAt(recruitment.getCreateAt());
        recruitmentDto.setUpdateAt(recruitment.getUpdateAt());
        recruitmentDto.setStatus(recruitment.getStatus());
        recruitmentDto.setCreateAt(recruitment.getCreateAt());
        recruitmentDto.setCreateAt(recruitment.getCreateAt());

        return recruitmentDto;
    }
}
