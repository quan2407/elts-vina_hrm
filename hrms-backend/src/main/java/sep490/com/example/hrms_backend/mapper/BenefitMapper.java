package sep490.com.example.hrms_backend.mapper;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import sep490.com.example.hrms_backend.dto.DepartmentDTO;
import sep490.com.example.hrms_backend.dto.benefit.EmployeeBasicDetailDTO;
import sep490.com.example.hrms_backend.dto.benefit.PositionRegistrationDTO;
import sep490.com.example.hrms_backend.dto.benefit.BenefitDTO;
import sep490.com.example.hrms_backend.dto.benefit.BenefitRegistrationDTO;
import sep490.com.example.hrms_backend.entity.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface BenefitMapper {

    BenefitDTO toBenefitDTO(Benefit benefit);

    @Mapping(source = "employee", target = "employee")
    BenefitRegistrationDTO toBenefitRegistrationDTO(BenefitRegistration reg);

    EmployeeBasicDetailDTO toEmployeeBasicDetailDTO(Employee employee);

    DepartmentDTO toDepartmentDTO(Department department);

    @Mapping(source = "position.positionId", target = "positionId")
    @Mapping(source = "position.positionName", target = "positionName")
    @Mapping(source = "position.departments", target = "departments")
    @Mapping(source = "formulaValue", target = "formulaValue")
    @Mapping(source = "formulaType", target = "formulaType")
    @Mapping(source = "registrations", target = "registrations")
    PositionRegistrationDTO toPositionRegistrationDTO(BenefitPosition benefitPosition);

    default List<PositionRegistrationDTO> toPositionRegistrationDTOs(List<BenefitPosition> bps) {
        if (bps == null) return new ArrayList<>();
        return bps.stream().map(this::toPositionRegistrationDTO).collect(Collectors.toList());
    }

    default List<BenefitDTO> toBenefitDTOs(List<Benefit> benefits) {
        if (benefits == null) return new ArrayList<>();
        return benefits.stream().map(this::toBenefitDTO).collect(Collectors.toList());
    }

    @AfterMapping
    default void mapPositions(@MappingTarget BenefitDTO dto, Benefit benefit) {
        dto.setPositions(toPositionRegistrationDTOs(benefit.getBenefitPositions()));
    }
}
