package sep490.com.example.hrms_backend.mapper;

import sep490.com.example.hrms_backend.dto.LinePMCDto;
import sep490.com.example.hrms_backend.entity.Line;

public class LinePMCMapper {
    public static LinePMCDto mapToLinePMCDto(Line line){
        LinePMCDto linePMCDto = new LinePMCDto();

        linePMCDto.setId(line.getLineId());
        linePMCDto.setName(line.getLineName());
        linePMCDto.setQuantity(line.getEmployees().size());
        linePMCDto.setLeaderName(line.getLeader().getEmployeeName());
        return linePMCDto;
    }
}
