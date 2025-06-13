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
import org.springframework.stereotype.Component;
import sep490.com.example.hrms_backend.exception.HRMSAPIException;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtTokenProvider {
    @Value("${app.jwt-secret}")
    private String jwtSecret;
    @Value("${app.jwt-expiration-milliseconds}")
    private long jwtExpirationDate;

    // ✅ Tạo JWT Token khi người dùng đăng nhập thành công
    public String generateToken(Authentication authentication){
        String username = authentication.getName(); // Lấy username của người dùng đã xác thực
        Date currentDate = new Date();              // Thời gian hiện tại
        Date expireDate = new Date(currentDate.getTime() + jwtExpirationDate); // Thời gian hết hạn

        // Xây dựng token với thông tin người dùng, thời gian tạo, thời gian hết hạn, và chữ ký bảo mật
        String token = Jwts.builder()
                .subject(username)                 // Subject chính là username
                .issuedAt(currentDate)             // Ghi lại thời gian phát hành
                .expiration(expireDate)            // Thiết lập thời gian hết hạn
                .signWith(key())                   // Dùng secret key để ký token
                .compact();                        // Build ra chuỗi JWT

        return token;
    }

    // 🔑 Hàm tạo khóa bảo mật để ký/giải mã token từ chuỗi base64 trong file config
    private SecretKey key(){
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    // 🧠 Lấy username (subject) từ chuỗi token đã mã hóa
    public String getUsername(String token){
        return Jwts.parser()
                .verifyWith(key())                     // Dùng khóa bí mật để verify token
                .build()
                .parseSignedClaims(token)              // Giải mã phần Claims (Payload) của token
                .getPayload()
                .getSubject();                         // Trả về username đã được lưu
    }

    // 🛡 Kiểm tra token có hợp lệ hay không
    public boolean validateToken(String token){
        try {
            Jwts.parser()
                    .verifyWith(key())                 // Dùng khóa bí mật để kiểm tra chữ ký
                    .build()
                    .parse(token);                     // Nếu không lỗi => token hợp lệ
            return true;
        } catch (MalformedJwtException malformedJwtException){
            // Token bị lỗi định dạng
            throw new HRMSAPIException(HttpStatus.BAD_REQUEST, "Invalid JWT Token");
        } catch (ExpiredJwtException expiredJwtException){
            // Token đã hết hạn
            throw new HRMSAPIException(HttpStatus.BAD_REQUEST,"Expired JWT Token");
        } catch (UnsupportedJwtException unsupportedJwtException){
            // Token không được hỗ trợ
            throw new HRMSAPIException(HttpStatus.BAD_REQUEST,"Unsupported JWT Token");
        } catch (IllegalArgumentException illegalArgumentException){
            // Token rỗng hoặc null
            throw new HRMSAPIException(HttpStatus.BAD_REQUEST,"JWT claims string is null or empty");
        }
    }
}
