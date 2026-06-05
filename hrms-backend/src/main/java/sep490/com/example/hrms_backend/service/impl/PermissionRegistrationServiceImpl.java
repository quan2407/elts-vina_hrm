package sep490.com.example.hrms_backend.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sep490.com.example.hrms_backend.entity.Permission;
import sep490.com.example.hrms_backend.entity.Role;
import sep490.com.example.hrms_backend.repository.PermissionRepository;
import sep490.com.example.hrms_backend.repository.RoleRepository;
import sep490.com.example.hrms_backend.service.PermissionRegistrationService;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;

@Service
@RequiredArgsConstructor
public class PermissionRegistrationServiceImpl implements PermissionRegistrationService {

    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;

    @Override
    @Transactional
    public void registerPermission(String apiPath, String method, String module, Collection<String> roleNames) {
        Permission permission = permissionRepository
                .findByApiPathAndMethod(apiPath, method)
                .orElseGet(() -> {
                    Permission p = Permission.builder()
                            .apiPath(apiPath)
                            .method(method)
                            .module(module)
                            .name(method + " " + apiPath)
                            .createdAt(LocalDateTime.now())
                            .build();
                    return permissionRepository.save(p);
                });

        if (!roleNames.contains("ROLE_ADMIN")) {
            roleNames = new HashSet<>(roleNames);
            ((HashSet<String>) roleNames).add("ROLE_ADMIN");
        }

        for (String roleName : roleNames) {
            Role role = roleRepository.findByRoleName(roleName)
                    .orElseGet(() -> roleRepository.save(
                            Role.builder().roleName(roleName).build()
                    ));

            if (role.getPermissions() == null) {
                role.setPermissions(new HashSet<>());
            }

            if (!role.getPermissions().contains(permission)) {
                role.getPermissions().add(permission);
                roleRepository.save(role);
            }
        }
    }
}
