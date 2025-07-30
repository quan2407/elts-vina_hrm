package sep490.com.example.hrms_backend.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import sep490.com.example.hrms_backend.service.PermissionRegistrationService;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PermissionBootstrapper implements ApplicationRunner {

    private final PermissionRegistrationService permissionRegistrationService;

    @Override
    public void run(ApplicationArguments args) {
        // Các quyền truy cập mặc định cần được khởi tạo khi khởi động hệ thống
        List<String> allRoles = List.of(
                "ROLE_ADMIN",
                "ROLE_HR",
                "ROLE_HR_MANAGER",
                "ROLE_LINE_LEADER",
                "ROLE_EMPLOYEE",
                "ROLE_PMC",
                "ROLE_PRODUCTION_MANAGER"
        );

        permissionRegistrationService.registerPermission(
                "/api/me/permissions",
                "GET",
                "Permission",
                allRoles
        );
        permissionRegistrationService.registerPermission(
                "/api/permissions/grouped",
                "GET",
                "Permission",
                List.of("ROLE_ADMIN")
        );

        permissionRegistrationService.registerPermission(
                "/api/roles",
                "GET",
                "Role",
                List.of("ROLE_ADMIN")
        );
        permissionRegistrationService.registerPermission(
                "/api/attendances/import",
                "POST",
                "Attendance",
                List.of("ROLE_HR", "ROLE_HR_MANAGER")
        );
        permissionRegistrationService.registerPermission(
                "/api/attendances/export",
                "POST",
                "Attendance",
                List.of("ROLE_HR", "ROLE_HR_MANAGER")
        );
        permissionRegistrationService.registerPermission(
                "/api/dashboard/employee-gender-distribution",
                "GET",
                "Employee",
                List.of("ROLE_HR_MANAGER", "ROLE_HR")
        );

        permissionRegistrationService.registerPermission(
                "/api/dashboard/employee-department-distribution",
                "GET",
                "Employee",
                List.of("ROLE_HR_MANAGER", "ROLE_HR")
        );
        permissionRegistrationService.registerPermission(// ← đây là method
                "/api/applications/admin",
                "POST",// ← đây là apiPath
                "Application",
                List.of("ROLE_HR_MANAGER", "ROLE_HR", "ROLE_PRODUCTION_MANAGER")
        );


        permissionRegistrationService.registerPermission(
                "/api/employees/simple",
                "GET",
                "Employee",
                List.of("ROLE_HR_MANAGER", "ROLE_HR", "ROLE_PRODUCTION_MANAGER")
        );

        permissionRegistrationService.registerPermission(
                "/api/candidate/*",
                "GET",
                "Role",
                List.of("ROLE_HR","ROLE_PRODUCTION_MANAGER", "ROLE_HR_MANAGER")
        );
        permissionRegistrationService.registerPermission(
                "/api/work-schedule-details/export-work-schedule",
                "POST",
                "Work Schedule",
                List.of("ROLE_HR", "ROLE_HR_MANAGER", "ROLE_PRODUCTION_MANAGER")
        );

    }
}
