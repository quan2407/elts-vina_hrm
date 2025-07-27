package sep490.com.example.hrms_backend.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import sep490.com.example.hrms_backend.entity.Permission;
import sep490.com.example.hrms_backend.entity.Role;
import sep490.com.example.hrms_backend.repository.PermissionRepository;
import sep490.com.example.hrms_backend.repository.RoleRepository;

import java.util.*;

@Component
@RequiredArgsConstructor
public class ApiPermissionInitializer implements ApplicationRunner {

    private final RequestMappingHandlerMapping handlerMapping;
    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;

    @Override
    public void run(ApplicationArguments args) {
        Map<RequestMappingInfo, ?> map = handlerMapping.getHandlerMethods();

        Set<String> ignoredEndpoints = Set.of(
                "POST /api/auth/login",
                "POST /api/auth/reset-password",
                "POST /api/auth/request-reset-password",
                "GET /api/recruitment",
                "GET /api/recruitment/{id:\\d+}",
                "POST /api/candidate/apply/**",
                "ANY /uploads/**"
        );

        Set<Permission> newPermissions = new HashSet<>();

        for (RequestMappingInfo info : map.keySet()) {
            Set<String> patterns = info.getPatternsCondition().getPatterns();
            Set<RequestMethod> methods = info.getMethodsCondition().getMethods();

            if (methods.isEmpty()) {
                methods = Set.of(RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT,
                        RequestMethod.DELETE, RequestMethod.PATCH); // fallback nếu không khai báo cụ thể
            }

            for (String pattern : patterns) {
                for (RequestMethod method : methods) {
                    String name = method.name() + " " + pattern;

                    if (ignoredEndpoints.contains(name) || ignoredEndpoints.contains("ANY " + pattern)) {
                        continue;
                    }

                    if (permissionRepository.findByName(name).isEmpty()) {
                        Permission permission = new Permission(name);
                        permissionRepository.save(permission);
                        newPermissions.add(permission);
                    }
                }
            }
        }

        if (!newPermissions.isEmpty()) {
            Role adminRole = roleRepository.findByName("ROLE_ADMIN")
                    .orElseGet(() -> roleRepository.save(new Role("ROLE_ADMIN")));

            adminRole.getPermissions().addAll(newPermissions);
            roleRepository.save(adminRole);
        }
    }


}
