package sep490.com.example.hrms_backend.mapper;

import sep490.com.example.hrms_backend.dto.RecruitmentDto;
import sep490.com.example.hrms_backend.entity.CandidateRecruitment;
import sep490.com.example.hrms_backend.entity.Recruitment;

import java.util.List;
import java.util.stream.Collectors;

public class RecruitmentMapper {
    public static RecruitmentDto mapToRecruitmentDto(Recruitment recruitment, RecruitmentDto recruitmentDto) {
        recruitmentDto.setRecruitmentId(recruitment.getId());
        recruitmentDto.setTitle(recruitment.getTitle());
        recruitmentDto.setEmploymentType(recruitment.getEmploymentType());
        recruitmentDto.setJobDescription(recruitment.getJobDescription());
        recruitmentDto.setJobRequirement(recruitment.getJobRequirement());
        recruitmentDto.setBenefits(recruitment.getBenefits());
        recruitmentDto.setMinSalary(recruitment.getMinSalary());
        recruitmentDto.setMaxSalary(recruitment.getMaxSalary());
        recruitmentDto.setQuantity(recruitment.getQuantity());
        recruitmentDto.setExpiredAt(recruitment.getExpiredAt());
        recruitmentDto.setCreateAt(recruitment.getCreateAt());
        recruitmentDto.setUpdateAt(recruitment.getUpdateAt());
        recruitmentDto.setStatus(recruitment.getStatus());
        recruitmentDto.setDepartmentId(recruitment.getDepartment().getDepartmentId());
        recruitmentDto.setCreatedById(recruitment.getCreatedBy().getEmployeeId());
        if (recruitment.getCandidateRecruitments() != null) {
            recruitmentDto.setCandidateRecruitmentsId(
                    recruitment.getCandidateRecruitments().stream()
                            .map(CandidateRecruitment::getId)
                            .collect(Collectors.toList())
            );
        }
        return recruitmentDto;
    }

    public static List<RecruitmentDto> mapToRecruitmentDtoList(List<Recruitment> recruitmentList) {
        return recruitmentList.stream().map(recruitment -> {
            RecruitmentDto dto = new RecruitmentDto();
            return mapToRecruitmentDto(recruitment, dto);
        }).toList();
    }

    public static Recruitment mapToRecruitment(Recruitment recruitment, RecruitmentDto recruitmentDto) {
        recruitment.setId(recruitmentDto.getRecruitmentId());
        recruitment.setTitle(recruitmentDto.getTitle());
        recruitment.setEmploymentType(recruitmentDto.getEmploymentType());
        recruitment.setJobDescription(recruitmentDto.getJobDescription());
        recruitment.setJobRequirement(recruitmentDto.getJobRequirement());
        recruitment.setBenefits(recruitmentDto.getBenefits());
        recruitment.setMinSalary(recruitmentDto.getMinSalary());
        recruitment.setMaxSalary(recruitmentDto.getMaxSalary());
        recruitment.setQuantity(recruitmentDto.getQuantity());
        recruitment.setExpiredAt(recruitmentDto.getExpiredAt());
        recruitment.setCreateAt(recruitmentDto.getCreateAt());
        recruitment.setUpdateAt(recruitmentDto.getUpdateAt());
        recruitment.setStatus(recruitmentDto.getStatus());

        return recruitment;
    }
}
