package com.tinqinacademy.authentication.core.services.security;

import com.tinqinacademy.authentication.api.operations.exceptions.InvalidJwtException;
import com.tinqinacademy.authentication.persistence.repositories.BlackListTokenRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class JwtService {
    @Value("${security.jwt.secret-key}")
    private String secretKey;

    @Value("${security.jwt.expiration-time}")
    private Long jwtExpiration;

    private final BlackListTokenRepository blackListTokenRepository;

    private Long getExpirationTime() {
        return jwtExpiration;
    }

    public String generateToken(
            Map<String, String> claims
    ) {
        return Jwts
                .builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + getExpirationTime()))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractUserId(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("user_id").toString();
    }

    public Claims extractAllClaims(String token) {
        try {
            return Jwts
                    .parserBuilder()
                    .setSigningKey(getSignInKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception ex) {
            throw new InvalidJwtException("JWT is not valid");
        }
    }

    public boolean isTokenValid(String token) {
        try {
            return extractExpiration(token).after(new Date(System.currentTimeMillis()))
                    && blackListTokenRepository.findByToken(token).isEmpty();
        } catch (InvalidJwtException ex) {
            return false;
        }
    }

    private Date extractExpiration(String token) {
        Claims claims = extractAllClaims(token);
        return claims.getExpiration();
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}

