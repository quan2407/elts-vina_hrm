package sep490.com.example.hrms_backend.mapper;

import sep490.com.example.hrms_backend.dto.RecruitmentDto;
import sep490.com.example.hrms_backend.entity.Recruitment;

import java.util.List;
import java.util.Optional;

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
        recruitmentDto.setDepartmentName(recruitment.getDepartment().getDepartmentName());
        recruitmentDto.setCreatedByIdName(recruitment.getCreatedBy().getEmployeeName());

        return recruitmentDto;
    }

    public static List<RecruitmentDto> mapToRecruitmentDtoList(List<Recruitment> recruitmentList) {
        return recruitmentList.stream().map(recruitment -> {
            RecruitmentDto dto = new RecruitmentDto();
            return mapToRecruitmentDto(recruitment, dto);
        }).toList();
    }

    public static Recruitment mapToRecruitment(Recruitment recruitment, RecruitmentDto recruitmentDto) {
        recruitment.setTitle(recruitmentDto.getTitle());
        recruitment.setWorkLocation(recruitmentDto.getWorkLocation());
        recruitment.setEmploymentType(recruitmentDto.getEmploymentType());
        recruitment.setJobDescription(recruitmentDto.getJobDescription());
        recruitment.setJobRequirement(recruitmentDto.getJobRequirement());
        recruitment.setBenefits(recruitmentDto.getBenefits());
        recruitment.setSalaryRange(recruitmentDto.getSalaryRange());
        recruitment.setQuantity(recruitmentDto.getQuantity());
        recruitment.setExpiredAt(recruitmentDto.getExpiredAt());
        recruitment.setCreateAt(recruitmentDto.getCreateAt());
        recruitment.setUpdateAt(recruitmentDto.getUpdateAt());
        recruitment.setStatus(recruitmentDto.getStatus());

        return recruitment;
    }
}
