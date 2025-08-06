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
import java.util.Set;

@Component
@RequiredArgsConstructor
public class PermissionFilter extends OncePerRequestFilter {

    private final AccountRepository accountRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String requestPath = request.getRequestURI();
        String method = request.getMethod();
        String token = request.getHeader("Authorization");

        System.out.println("[PermissionFilter] Path: " + method + " " + requestPath);

        // ✅ Nếu là public path và không có token → skip
        if (!hasToken(token) && isPublicPath(requestPath, method)) {
            System.out.println("[PermissionFilter] Public path & no token → skip permission check.");
            filterChain.doFilter(request, response);
            return;
        }

        // ✅ Có token → kiểm tra quyền
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {
            System.out.println("[PermissionFilter] Not authenticated.");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Chưa đăng nhập");
            return;
        }

        String username = authentication.getName();
        System.out.println("[PermissionFilter] Authenticated user: " + username);

        Account account = accountRepository.findByUsernameWithPermissions(username).orElse(null);
        if (account == null || account.getRole() == null) {
            System.out.println("[PermissionFilter] Account or role not found.");
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Không có quyền");
            return;
        }

        Set<Permission> permissions = account.getRole().getPermissions();

        boolean allowed = permissions.stream().anyMatch(p ->
                matchPath(p.getApiPath(), requestPath) &&
                        p.getMethod().equalsIgnoreCase(method)
        );

        System.out.println("[PermissionFilter] Permission allowed: " + allowed);

        if (!allowed) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Bạn không có quyền truy cập API này");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private boolean hasToken(String token) {
        return token != null && token.startsWith("Bearer ");
    }

    private boolean matchPath(String pattern, String path) {
        if (pattern.endsWith("/**")) {
            String prefix = pattern.substring(0, pattern.length() - 3);
            return path.startsWith(prefix);
        }

        if (pattern.contains("*")) {
            String regex = pattern.replace("*", "[^/]+");
            return path.matches(regex);
        }

        return pattern.equals(path);
    }

    private boolean isPublicPath(String requestPath, String method) {
        if (requestPath.equals("/api/auth/login") && method.equalsIgnoreCase("POST")) return true;
        if (requestPath.equals("/api/auth/reset-password") && method.equalsIgnoreCase("POST")) return true;
        if (requestPath.equals("/api/auth/request-reset-password") && method.equalsIgnoreCase("POST")) return true;
        if (requestPath.startsWith("/uploads/")) return true;
        if (requestPath.startsWith("/api/candidate/apply/") && method.equalsIgnoreCase("POST")) return true;
        if (requestPath.equals("/api/recruitment") && method.equalsIgnoreCase("GET")) return true;
        if (requestPath.matches("^/api/recruitment/\\d+$") && method.equalsIgnoreCase("GET")) return true;
        return false;
    }
}
