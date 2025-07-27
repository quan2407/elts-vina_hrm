package sep490.com.example.hrms_backend.mapper;

import sep490.com.example.hrms_backend.dto.RoleDTO;
import sep490.com.example.hrms_backend.entity.Role;

public class RoleMapper {
    public static RoleDTO toDTO(Role role) {
        RoleDTO dto = new RoleDTO();
        dto.setId(role.getRoleId());
        dto.setName(role.getRoleName());
        dto.setDescription(role.getDescription());
        return dto;
    }
}
