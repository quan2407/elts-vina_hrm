package sep490.com.example.hrms_backend.service;

import sep490.com.example.hrms_backend.dto.PermissionDTO;
import sep490.com.example.hrms_backend.dto.RoleDTO;

import java.util.List;
import java.util.Set;

public interface RoleService {
    void updatePermissions(Long roleId, Set<Long> permissionIds);
    Set<PermissionDTO> getPermissionsByRole(Long roleId);
    List<RoleDTO> getAllRoles();

}
