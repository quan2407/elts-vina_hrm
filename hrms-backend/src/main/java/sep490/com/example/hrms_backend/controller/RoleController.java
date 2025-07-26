package sep490.com.example.hrms_backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import sep490.com.example.hrms_backend.dto.PermissionDTO;
import sep490.com.example.hrms_backend.dto.RolePermissionRequestDTO;
import sep490.com.example.hrms_backend.service.RoleService;

import java.util.Set;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @PutMapping("/{id}/permissions")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateRolePermissions(
            @PathVariable Long id,
            @RequestBody RolePermissionRequestDTO requestDTO
    ) {
        roleService.updatePermissions(id, requestDTO.getPermissionIds());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/permissions")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Set<PermissionDTO>> getPermissionsByRole(@PathVariable Long id) {
        return ResponseEntity.ok(roleService.getPermissionsByRole(id));
    }
}
