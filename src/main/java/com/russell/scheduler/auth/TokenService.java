package com.russell.scheduler.auth;

import com.russell.scheduler.auth.dtos.Principal;
import com.russell.scheduler.common.exceptions.AuthTokenParseException;
import com.russell.scheduler.common.exceptions.InvalidJWTException;
import com.russell.scheduler.common.exceptions.MissingAuthTokenException;
import com.russell.scheduler.user.UserRole;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.naming.AuthenticationException;
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
                .setIssuer("Project-Scheduler")
                .claim("role", subject.getAuthUserRole())
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now+ config.getExpiration()))
                .signWith(config.getSigningKey());

        return tokenBuilder.compact();
    }

    public boolean isTokenValid(String token) {
        try {
            extractTokenDetails(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Principal extractTokenDetails(String token) {
        if (token == null || token.isEmpty()) {
            throw new MissingAuthTokenException();
        }

        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(config.getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            return new Principal(claims.getId(), claims.get("role", String.class));

        } catch(ExpiredJwtException e) {
            throw new InvalidJWTException("The provided auth token has expired");
        } catch (Exception e) {
            System.out.println("Exception in claims building");
            throw new AuthTokenParseException("Unknown error parsing auth token");
        }
    }
}
