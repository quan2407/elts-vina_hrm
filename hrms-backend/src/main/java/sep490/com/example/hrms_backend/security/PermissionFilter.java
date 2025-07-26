package sep490.com.example.hrms_backend.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import sep490.com.example.hrms_backend.entity.Account;
import sep490.com.example.hrms_backend.entity.Permission;
import sep490.com.example.hrms_backend.repository.AccountRepository;

import java.io.IOException;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class PermissionFilter extends OncePerRequestFilter {

    private final AccountRepository accountRepository;

    private static final List<String> PUBLIC_PATHS = List.of(
            "/api/auth/",
            "/uploads/",
            "/api/candidate/apply/"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String requestPath = request.getRequestURI();
        String method = request.getMethod();

        // Log request nhận được
        System.out.println("[PermissionFilter] Request: " + method + " " + requestPath);

        if (isPublicPath(requestPath, method)) {
            System.out.println("[PermissionFilter] Public path: Skip permission check.");
            filterChain.doFilter(request, response);
            return;
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("[PermissionFilter] Authentication: " + authentication);

        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            System.out.println("[PermissionFilter] Authentication failed or anonymous.");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Chưa đăng nhập");
            return;
        }

        String username = authentication.getName();
        System.out.println("[PermissionFilter] Username: " + username);

        Account account = accountRepository.findByUsernameWithPermissions(username).orElse(null);

        if (account == null || account.getRole() == null) {
            System.out.println("[PermissionFilter] Account or role not found for user: " + username);
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Không có quyền");
            return;
        }

        Set<Permission> permissions = account.getRole().getPermissions();
        System.out.println("[PermissionFilter] Permissions from DB: " + permissions);

        boolean allowed = permissions.stream().anyMatch(p ->
                matchPath(p.getApiPath(), requestPath) &&
                        p.getMethod().equalsIgnoreCase(method)
        );

        System.out.println("[PermissionFilter] Allowed: " + allowed);

        if (!allowed) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Bạn không có quyền truy cập API này");
            return;
        }

        filterChain.doFilter(request, response);
    }


    private boolean matchPath(String pattern, String path) {
        if (pattern.endsWith("/**")) {
            String prefix = pattern.substring(0, pattern.length() - 3);
            return path.startsWith(prefix);
        }

        if (pattern.contains("*")) {
            // Chuyển * thành regex: /api/employees/* => /api/employees/[^/]+
            String regex = pattern.replace("*", "[^/]+");
            return path.matches(regex);
        }

        return pattern.equals(path);
    }


    private boolean isPublicPath(String requestPath, String method) {
        // Auth APIs (public)
        if (requestPath.equals("/api/auth/login") && method.equalsIgnoreCase("POST")) return true;
        if (requestPath.equals("/api/auth/reset-password") && method.equalsIgnoreCase("POST")) return true;
        if (requestPath.equals("/api/auth/request-reset-password") && method.equalsIgnoreCase("POST")) return true;

        // Uploads
        if (requestPath.startsWith("/uploads/")) return true;

        // Candidate apply
        if (requestPath.startsWith("/api/candidate/apply/") && method.equalsIgnoreCase("POST")) return true;

        // Public recruitment view
        if (requestPath.equals("/api/recruitment") && method.equalsIgnoreCase("GET")) return true;
        if (requestPath.matches("^/api/recruitment/\\d+$") && method.equalsIgnoreCase("GET")) return true;

        return false;
    }


}

