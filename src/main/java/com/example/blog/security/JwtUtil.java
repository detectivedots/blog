package com.example.blog.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.JwtException;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {
    private final String secret = "1bf071c37dfd021e23147206cf5b5ceee69280e6307eff0275d39eca44b41c55b541fdfc";

    public String generateToken(String username) {
        long now = System.currentTimeMillis();
        Date issuedAt = new Date(now);
        Date expiry = new Date(now + 1000L * 60 * 60 * 10); // 10 hours

        return Jwts.builder().setSubject(username).setIssuedAt(issuedAt).setExpiration(expiry).signWith(SignatureAlgorithm.HS256, secret.getBytes(StandardCharsets.UTF_8)).compact();
    }

    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser().setSigningKey(secret.getBytes(StandardCharsets.UTF_8)).parseClaimsJws(token).getBody();
    }

    public String extractUsername(String token) {
        try {
            return getAllClaimsFromToken(token).getSubject();
        } catch (JwtException | IllegalArgumentException ex) {
            return null;
        }
    }

    public boolean validateToken(String token, String username) {
        try {
            String extracted = extractUsername(token);
            if (extracted == null) return false;
            Date expiration = getAllClaimsFromToken(token).getExpiration();
            return extracted.equals(username) && expiration.after(new Date());
        } catch (JwtException | IllegalArgumentException ex) {
            return false;
        }
    }
}
