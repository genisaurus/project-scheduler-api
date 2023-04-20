package com.russell.scheduler.auth;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.postgresql.shaded.com.ongres.scram.common.bouncycastle.base64.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;

@Component
public class JwtConfig {

    @Value("${jwt.secret}")
    private String salt;
    @Value("#{24*60*60*1000}") // ms in a day
    private int expiration;
    private final SignatureAlgorithm alg = SignatureAlgorithm.HS256;
    private Key signingKey;

    @PostConstruct // run after JwtConfig bean is created
    public void createSigningKey() {
        byte[] saltyBytes = Base64.encode(salt.getBytes());
        signingKey = Keys.secretKeyFor(alg);
    }

    public int getExpiration() {
        return expiration;
    }

    public SignatureAlgorithm getAlg() {
        return alg;
    }

    public Key getSigningKey() {
        return signingKey;
    }
}
