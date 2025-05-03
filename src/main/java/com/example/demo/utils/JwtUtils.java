package com.example.demo.utils;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;

@Component
public class JwtUtils {
    private final String SECRET_KEY = "emiuanhleuleuleuleuleuleuleuleuleu";
    private static final Long ACCESS_TOKEN_VALIDITY_SECONDS = 5 * 60 * 60L; // 5 hours

    // phương thức để lấy secret key chuyển từ string sang kiểu secret key
    private SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    // phương thức để tạo jsonwebtoken
    public String generateAccessToken(UserDetails userDetails) {
        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .claim("role", userDetails.getAuthorities().stream()
                                .map(authority -> authority.getAuthority()).toList())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_VALIDITY_SECONDS * 1000))
                .signWith(getSecretKey()) // tạo signature cho jsonwebtoken và header
                .compact()
                ;
    }
    // phương thức để xác thực jsonwebtoken
    public boolean validateToken(String token) {
        try {
            // kiểm tra xem token có hợp lệ hay không và kiểm tra xem token có hết hạn hay không
            Jwts.parser().setSigningKey(getSecretKey()).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // phương thức để lấy thông tin gmail từ jsonwebtoken
    public String getEmailFromToken(String token) {
        return Jwts.parser().setSigningKey(getSecretKey()).build().parseClaimsJws(token).getBody().getSubject();
    }

    // phương thức để lấy role từ jsonwebtoken
    public List<String> getRoleFromToken(String token) {
        // "role": ["ADMIN"], "role": ["MANAGER"]
        return Jwts.parser().setSigningKey(getSecretKey()).build().parseClaimsJws(token).getBody().get("role", List.class);
    }

}
