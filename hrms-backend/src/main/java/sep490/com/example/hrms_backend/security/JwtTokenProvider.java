package sep490.com.example.hrms_backend.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import sep490.com.example.hrms_backend.exception.HRMSAPIException;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class JwtTokenProvider {

    @Value("${app.jwt-secret}")
    private String jwtSecret;

    @Value("${app.jwt-expiration-milliseconds}")
    private long jwtExpirationDate;

    // ✅ Tạo JWT Token khi người dùng đăng nhập thành công
    public String generateToken(Authentication authentication){
        String username = authentication.getName();
        Date currentDate = new Date();
        Date expireDate = new Date(currentDate.getTime() + jwtExpirationDate);

        // Lấy danh sách role của user
        List<String> roles = authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        // Tạo claims để nhúng roles vào payload
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", roles);

        // Tạo JWT token
        String token = Jwts.builder()
                .claims(claims)          // Gắn thêm claims (roles)
                .subject(username)       // Subject chính là username
                .issuedAt(currentDate)   // Thời gian phát hành
                .expiration(expireDate)  // Thời gian hết hạn
                .signWith(key())         // Ký với secret key
                .compact();

        return token;
    }

    // 🔑 Tạo khóa bí mật từ secret string
    private SecretKey key(){
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    // 🧠 Lấy username từ token
    public String getUsername(String token){
        return Jwts.parser()
                .verifyWith(key())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    // 🛡 Kiểm tra token hợp lệ
    public boolean validateToken(String token){
        try {
            Jwts.parser()
                    .verifyWith(key())
                    .build()
                    .parse(token);
            return true;
        } catch (MalformedJwtException e) {
            throw new HRMSAPIException(HttpStatus.BAD_REQUEST, "Invalid JWT Token");
        } catch (ExpiredJwtException e) {
            throw new HRMSAPIException(HttpStatus.BAD_REQUEST, "Expired JWT Token");
        } catch (UnsupportedJwtException e) {
            throw new HRMSAPIException(HttpStatus.BAD_REQUEST, "Unsupported JWT Token");
        } catch (IllegalArgumentException e) {
            throw new HRMSAPIException(HttpStatus.BAD_REQUEST, "JWT claims string is null or empty");
        }
    }
}
