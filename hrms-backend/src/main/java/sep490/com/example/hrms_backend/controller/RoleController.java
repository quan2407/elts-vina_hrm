package sep490.com.example.hrms_backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sep490.com.example.hrms_backend.dto.PermissionDTO;
import sep490.com.example.hrms_backend.dto.RoleDTO;
import sep490.com.example.hrms_backend.dto.RolePermissionRequestDTO;
import sep490.com.example.hrms_backend.service.PermissionRegistrationService;
import sep490.com.example.hrms_backend.service.RoleService;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;
    private final PermissionRegistrationService permissionRegistrationService;
    @PutMapping("/{id}/permissions")
    public ResponseEntity<?> updateRolePermissions(
            @PathVariable Long id,
            @RequestBody RolePermissionRequestDTO requestDTO
    ) {
        roleService.updatePermissions(id, requestDTO.getPermissionIds());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/permissions")
    public ResponseEntity<Set<PermissionDTO>> getPermissionsByRole(@PathVariable Long id) {
        return ResponseEntity.ok(roleService.getPermissionsByRole(id));
    }
    @GetMapping
    public ResponseEntity<List<RoleDTO>> getAllRoles() {
        permissionRegistrationService.registerPermission(
                "/api/roles",
                "GET",
                "Role",
                List.of("ROLE_ADMIN")
        );

        return ResponseEntity.ok(roleService.getAllRoles());
    }
}
