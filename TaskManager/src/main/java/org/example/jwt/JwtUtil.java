package org.example.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Component
@NoArgsConstructor
@AllArgsConstructor
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration:86400000}")
    private long expiration;

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    Claims parseClaims(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (JwtException e) {
            throw new RuntimeException("Invalid JWT token", e);
        }
    }

    public UUID extractUserId(String token) {
        Claims c = parseClaims(token);
        return UUID.fromString(c.getSubject());
    }

    public boolean isTokenExpired(String token) {
        Date exp = parseClaims(token).getExpiration();
        return exp.before(new Date());
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        try {
            UUID id = extractUserId(token);
            String email = parseClaims(token).get("email", String.class);
            return email != null && email.equals(userDetails.getUsername()) && !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }

    public String generateToken(UserDetails userDetails, UUID userId) {
        Instant now = Instant.now();
        return Jwts.builder()
                .setSubject(userId.toString())
                .claim("email", userDetails.getUsername())
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusMillis(expiration)))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

}
