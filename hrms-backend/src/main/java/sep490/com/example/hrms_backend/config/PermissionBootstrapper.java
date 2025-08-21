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
                "Recruitment",
                List.of("ROLE_HR", "ROLE_PRODUCTION_MANAGER", "ROLE_HR_MANAGER")
        );

        permissionRegistrationService.registerPermission(
                "/api/notification",
                "GET",
                "Notification",
                allRoles
        );

        permissionRegistrationService.registerPermission(
                "/api/notification/*/read",
                "PATCH",
                "Notification",
                allRoles
        );
        permissionRegistrationService.registerPermission(
                "/api/work-schedule-details/export-work-schedule",
                "POST",
                "Work Schedule",
                List.of("ROLE_HR", "ROLE_HR_MANAGER", "ROLE_PRODUCTION_MANAGER")
        );
        permissionRegistrationService.registerPermission(
                "/api/salaries/export",
                "GET",
                "Salary",
                List.of("ROLE_HR", "ROLE_HR_MANAGER")
        );
        permissionRegistrationService.registerPermission(
                "/api/username/me/name",
                "GET",
                "Profile",
                allRoles
        );
        permissionRegistrationService.registerPermission(
                "/api/employees/department/*",
                "GET",
                "Employee",
                allRoles
        );
        permissionRegistrationService.registerPermission(
                "/api/me",
                "GET",
                "Employee",
                allRoles
        );
        permissionRegistrationService.registerPermission(
                "/api/interview/non-Hr/*",
                "PUT",
                "Interview",
                allRoles);
        permissionRegistrationService.registerPermission("/api/hr/benefits", "GET", "Benefit", List.of("ROLE_HR", "ROLE_HR_MANAGER"));
        permissionRegistrationService.registerPermission("/api/hr/benefits/*", "GET", "Benefit", List.of("ROLE_HR", "ROLE_HR_MANAGER"));
        permissionRegistrationService.registerPermission("/api/hr/benefit/*/position/*", "GET", "Benefit", List.of("ROLE_HR", "ROLE_HR_MANAGER"));
        permissionRegistrationService.registerPermission("/api/hr/benefit/*", "GET", "Benefit", List.of("ROLE_HR", "ROLE_HR_MANAGER"));
        permissionRegistrationService.registerPermission("/api/hr/benefits/*/available-positions", "GET", "Benefit", List.of("ROLE_HR", "ROLE_HR_MANAGER"));
        permissionRegistrationService.registerPermission("/api/hr/benefits/assign", "POST", "Benefit", List.of("ROLE_HR", "ROLE_HR_MANAGER"));
        permissionRegistrationService.registerPermission("/api/hr/benefits/unassign/benefit/*/position/*", "DELETE", "Benefit", List.of("ROLE_HR", "ROLE_HR_MANAGER"));
        permissionRegistrationService.registerPermission("/api/hr/benefits/formula", "PUT", "Benefit", List.of("ROLE_HR", "ROLE_HR_MANAGER"));
        permissionRegistrationService.registerPermission("/api/hr/benefits", "POST", "Benefit", List.of("ROLE_HR", "ROLE_HR_MANAGER"));
        permissionRegistrationService.registerPermission("/api/hr/benefits/*", "PATCH", "Benefit", List.of("ROLE_HR", "ROLE_HR_MANAGER"));
        permissionRegistrationService.registerPermission("/api/*", "PATCH", "Benefit", List.of("ROLE_HR", "ROLE_HR_MANAGER")); // (status toggle)
        permissionRegistrationService.registerPermission("/api/keyword/*", "GET", "Benefit", List.of("ROLE_HR", "ROLE_HR_MANAGER"));
        permissionRegistrationService.registerPermission("/api/hr/benefits/*", "DELETE", "Benefit", List.of("ROLE_HR", "ROLE_HR_MANAGER"));
        permissionRegistrationService.registerPermission("/api/hr/benefit/position/*", "GET", "Benefit", List.of("ROLE_HR", "ROLE_HR_MANAGER"));
// [1] GET /api/employees/{employeeId}
        permissionRegistrationService.registerPermission("/api/employees/*", "GET", "BenefitRegistration", allRoles);

// [2] POST /api/hr/benefits/quick-register
        permissionRegistrationService.registerPermission("/api/hr/benefits/quick-register", "POST", "BenefitRegistration", List.of("ROLE_HR", "ROLE_HR_MANAGER"));

// [3] DELETE /api/hr/benefits/un-register/benefit/{benefitId}/position/{positionId}/employee/{employeeId}
        permissionRegistrationService.registerPermission("/api/hr/benefits/un-register/benefit/*/position/*/employee/*", "DELETE", "BenefitRegistration", List.of("ROLE_HR", "ROLE_HR_MANAGER"));

// [4] GET /api/hr/benefits/search-unregistered
        permissionRegistrationService.registerPermission("/api/hr/benefits/search-unregistered", "GET", "BenefitRegistration", List.of("ROLE_HR", "ROLE_HR_MANAGER"));

        permissionRegistrationService.registerPermission("/api/hr/benefits/quick-register-all", "POST", "BenefitRegistration", List.of("ROLE_HR", "ROLE_HR_MANAGER"));

        permissionRegistrationService.registerPermission("/api/hr/benefits/multi-un-register/benefit/*/position/*", "DELETE", "BenefitRegistration", List.of("ROLE_HR", "ROLE_HR_MANAGER"));

        permissionRegistrationService.registerPermission("/api/hr/benefit/*/position/*/stats", "GET", "BenefitRegistration", List.of("ROLE_HR", "ROLE_HR_MANAGER"));

        permissionRegistrationService.registerPermission(
                "/api/positions",
                "GET",
                "Position",
                allRoles
        );
        permissionRegistrationService.registerPermission(
                "/api/applications/*/cancel",
                "POST",
                "Application",
                allRoles
        );
        permissionRegistrationService.registerPermission(
                "/api/employees/add-to-line/*",
                "PUT",
                "Employee",
                List.of("ROLE_PMC")
        );
        permissionRegistrationService.registerPermission(
                "/api/attendances/check-schedule-coverage",
                "GET",
                "Attendance",
                allRoles
        );

    }
}
