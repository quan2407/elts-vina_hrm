package sep490.com.example.hrms_backend.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@AllArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private JwtTokenProvider jwtTokenProvider;
    private UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String requestPath = request.getRequestURI();
        String method = request.getMethod();
        String token = getTokenFromRequest(request);

        System.out.println("[JwtAuthenticationFilter] Path: " + method + " " + requestPath);
        System.out.println("[JwtAuthenticationFilter] Token: " + token);

        // ✅ Nếu là public path VÀ không có token → bỏ qua xác thực
        if (!StringUtils.hasText(token) && isPublicPath(requestPath, method)) {
            filterChain.doFilter(request, response);
            return;
        }

        // ✅ Nếu có token → xác thực
        if (StringUtils.hasText(token) && jwtTokenProvider.validateToken(token)) {
            String username = jwtTokenProvider.getUsername(token);
            System.out.println("[JwtAuthenticationFilter] Username from token: " + username);

            try {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            } catch (Exception e) {
                System.out.println("[JwtAuthenticationFilter] Failed to load user: " + e.getMessage());
                SecurityContextHolder.clearContext(); // clear if something went wrong
            }
        }

        filterChain.doFilter(request, response);
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        return (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer "))
                ? bearerToken.substring(7)
                : null;
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
