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
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
@AllArgsConstructor
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private JwtTokenProvider jwtTokenProvider;
    private UserDetailsService userDetailsService;
    // Hàm này sẽ chạy mỗi lần có request đến (được gọi tự động)
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 🧾 Lấy JWT từ header
        String token = getTokenFromRequest(request);

        // ✅ Nếu có token và token hợp lệ thì xử lý xác thực
        if (StringUtils.hasText(token) && jwtTokenProvider.validateToken(token)) {

            // 🔍 Trích xuất username từ JWT
            String username = jwtTokenProvider.getUsername(token);

            // 🔄 Load thông tin chi tiết của user từ DB (hoặc bộ nhớ)
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            // 🛡️ Tạo đối tượng xác thực dựa trên userDetails
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );

            // Gắn thêm thông tin yêu cầu từ request (ví dụ IP, session, etc.)
            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            // 👮 Gán authentication vào SecurityContext (để các Controller dùng được user info)
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        }

        // Tiếp tục chuỗi filter
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
