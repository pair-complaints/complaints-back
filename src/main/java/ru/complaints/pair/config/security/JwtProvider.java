package ru.complaints.pair.config.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Slf4j
@Component
public class JwtProvider {
    private final SecretKey jwtAccessSecret;
    private final SecretKey jwtRefreshSecret;
    @Value("${jwt.access-expiration-min}")
    private long accessExpirationMin;
    @Value("${jwt.refresh-expiration-days}")
    private long refreshExpirationDays;

    public JwtProvider(
            @Value("${jwt.secret.access}") String jwtAccessSecret,
            @Value("${jwt.secret.refresh}") String jwtRefreshSecret
    ) {
        this.jwtAccessSecret = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtAccessSecret));
        this.jwtRefreshSecret = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtRefreshSecret));
    }

    /**
     * Генерация access токена
     */
    public String generateAccessToken(@NonNull UserDetails userDetails) {
        LocalDateTime now = LocalDateTime.now();
        Instant expiration = now.plusMinutes(accessExpirationMin).atZone(ZoneId.systemDefault()).toInstant();
        return Jwts.builder()
                .subject(userDetails.getUsername())
                .expiration(Date.from(expiration))
                .signWith(jwtAccessSecret)
                .compact();
    }

    /**
     * Генерация refresh токена
     */
    public String generateRefreshToken(@NonNull UserDetails userDetails) {
        LocalDateTime now = LocalDateTime.now();
        Instant expiration = now.plusDays(refreshExpirationDays).atZone(ZoneId.systemDefault()).toInstant();
        return Jwts.builder()
                .subject(userDetails.getUsername())
                .expiration(Date.from(expiration))
                .signWith(jwtRefreshSecret)
                .compact();
    }

    /**
     * Валидация access токена
     */
    public boolean validateAccessToken(@NonNull String token) {
        return validateToken(token, jwtAccessSecret);
    }

    /**
     * Валидация refresh токена
     */
    public boolean validateRefreshToken(@NonNull String token) {
        return validateToken(token, jwtRefreshSecret);
    }

    /**
     * Получение клеймов access токена
     */
    public Claims getAccessClaims(@NonNull String token) {
        return getClaims(token, jwtAccessSecret);
    }

    /**
     * Получения клеймов refresh токена
     */
    public Claims getRefreshClaims(@NonNull String token) {
        return getClaims(token, jwtRefreshSecret);
    }

    private boolean validateToken(@NonNull String token, @NonNull SecretKey secret) {
        try {
            Jwts.parser()
                    .verifyWith(secret)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (ExpiredJwtException expEx) {
            log.error("Token expired", expEx);
        } catch (UnsupportedJwtException unsEx) {
            log.error("Unsupported jwt", unsEx);
        } catch (MalformedJwtException mjEx) {
            log.error("Malformed jwt", mjEx);
        } catch (SignatureException sEx) {
            log.error("Invalid signature", sEx);
        } catch (Exception e) {
            log.error("invalid token", e);
        }
        return false;
    }

    private Claims getClaims(@NonNull String token, @NonNull SecretKey secret) {
        return Jwts.parser()
                .verifyWith(secret)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
