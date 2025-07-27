package sep490.com.example.hrms_backend.mapper;

import sep490.com.example.hrms_backend.dto.PermissionDTO;
import sep490.com.example.hrms_backend.entity.Permission;

public class PermissionMapper {

    public static PermissionDTO toDTO(Permission permission) {
        return PermissionDTO.builder()
                .id(permission.getId())
                .name(permission.getName())
                .apiPath(permission.getApiPath())
                .method(permission.getMethod())
                .module(permission.getModule())
                .build();
    }

    public static Permission toEntity(PermissionDTO dto) {
        return Permission.builder()
                .id(dto.getId())
                .name(dto.getName())
                .apiPath(dto.getApiPath())
                .method(dto.getMethod())
                .module(dto.getModule())
                .build();
    }
}
