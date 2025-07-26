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

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("[DEBUG] Authentication = " + SecurityContextHolder.getContext().getAuthentication());

        if (authentication == null || !authentication.isAuthenticated()) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Chưa đăng nhập");
            return;
        }

        String username = authentication.getName();
        Account account = accountRepository.findByUsernameWithPermissions(username).orElse(null);
        System.out.println(">> Role: " + account.getRole());
        System.out.println(">> Permissions:");
        account.getRole().getPermissions().forEach(p ->
                System.out.println("- " + p.getMethod() + " " + p.getApiPath())
        );


        if (account == null || account.getRole() == null) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Không có quyền");
            return;
        }

        Set<Permission> permissions = account.getRole().getPermissions();
        String requestPath = request.getRequestURI();
        String requestMethod = request.getMethod();

        boolean allowed = permissions.stream().anyMatch(p ->
                matchPath(p.getApiPath(), requestPath) &&
                        p.getMethod().equalsIgnoreCase(requestMethod)
        );

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
        return pattern.equals(path);
    }
}
