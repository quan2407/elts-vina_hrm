package sep490.com.example.hrms_backend.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sep490.com.example.hrms_backend.dto.PermissionDTO;
import sep490.com.example.hrms_backend.dto.RoleDTO;
import sep490.com.example.hrms_backend.entity.Permission;
import sep490.com.example.hrms_backend.entity.Role;
import sep490.com.example.hrms_backend.exception.ResourceNotFoundException;
import sep490.com.example.hrms_backend.mapper.PermissionMapper;
import sep490.com.example.hrms_backend.mapper.RoleMapper;
import sep490.com.example.hrms_backend.repository.PermissionRepository;
import sep490.com.example.hrms_backend.repository.RoleRepository;
import sep490.com.example.hrms_backend.service.RoleService;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    @Override
    @Transactional
    public void updatePermissions(Long roleId, Set<Long> permissionIds) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Role", "id", roleId));

        Set<Permission> permissions = permissionRepository.findAllById(permissionIds).stream().collect(Collectors.toSet());

        role.setPermissions(permissions);
        roleRepository.save(role);
    }

    @Override
    public Set<PermissionDTO> getPermissionsByRole(Long roleId) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Role", "id", roleId));
        role.getPermissions().size();

        return role.getPermissions().stream()
                .map(PermissionMapper::toDTO)
                .collect(Collectors.toSet());
    }
    @Override
    public List<RoleDTO> getAllRoles() {
        return roleRepository.findAll().stream()
                .map(RoleMapper::toDTO)
                .collect(Collectors.toList());
    }

}

