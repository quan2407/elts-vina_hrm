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
//@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private JwtTokenProvider jwtTokenProvider;
    private UserDetailsService userDetailsService;
    // Hàm này sẽ chạy mỗi lần có request đến (được gọi tự động)
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String token = getTokenFromRequest(request);
        System.out.println("[JwtAuthenticationFilter] Token: " + token);

        if (StringUtils.hasText(token)) {
            boolean validToken = jwtTokenProvider.validateToken(token);
            System.out.println("[JwtAuthenticationFilter] Token valid: " + validToken);

            if (validToken) {
                String username = jwtTokenProvider.getUsername(token);
                System.out.println("[JwtAuthenticationFilter] Username: " + username);

                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );

                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);

            } else {
                System.out.println("[JwtAuthenticationFilter] Invalid token!");
            }
        } else {
            System.out.println("[JwtAuthenticationFilter] No token found in request!");
        }

        filterChain.doFilter(request, response);
    }

    // 📦 Hàm hỗ trợ: Lấy token từ header
    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        // Kiểm tra header có bắt đầu bằng "Bearer " không
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            // Trả về phần sau từ "Bearer "
            return bearerToken.substring(7);
        }
        return null;
    }
}
