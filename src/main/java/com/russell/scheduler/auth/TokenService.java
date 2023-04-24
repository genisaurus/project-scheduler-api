package com.russell.scheduler.auth;

import com.russell.scheduler.auth.JwtConfig;
import com.russell.scheduler.auth.dtos.Principal;
import com.russell.scheduler.entities.UserRole;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
public class TokenService {

    private final JwtConfig config;

    @Autowired
    public TokenService(JwtConfig config) {
        this.config = config;
    }

    public String generateToken(Principal subject) {
        long now = System.currentTimeMillis();
        JwtBuilder tokenBuilder = Jwts.builder()
                .setId(subject.getAuthUserId())
                .setIssuer("Project Scheduler")
                .claim("role", subject.getAuthUserRole())
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now+ config.getExpiration()))
                .signWith(config.getAlg(), config.getSigningKey());

        return tokenBuilder.compact();
    }

    public boolean isTokenValid(String token) {
        return false;
    }

    public Optional<Principal> extractTokenDetails(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(config.getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            return Optional.of(new Principal(claims.getId(), claims.get("role", UserRole.class)));

        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }
}
