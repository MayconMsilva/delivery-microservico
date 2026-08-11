package com.delivery_beta.auth_service.security;


import com.delivery_beta.auth_service.exception.InvalidTokenException;
import com.delivery_beta.auth_service.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtService {


    private final SecretKey secretKey;
    private final long expirationSeconds;

    public JwtService(
            @Value("${jwt.secret}")
            String secret,

            @Value("${jwt.expiration-seconds}")
            long expirationSeconds
    ){
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes());
        this.expirationSeconds = expirationSeconds;
    }

    public String generateToken(User user){
        Date now = new Date();
        Date expiration = new Date(now.getTime() + expirationSeconds * 1000);

        return Jwts.builder()
                .subject(user.getId().toString())
                .claim("email", user.getEmail())
                .claim("role", user.getRole().name())
                .issuedAt(now)
                .expiration(expiration)
                .signWith(secretKey)
                .compact();
    }

    public long getExpirationSeconds(){
        return expirationSeconds;
    }

    public Claims extractClaims(String token){
        try{
            return Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        }catch(ExpiredJwtException e){
            throw new InvalidTokenException("Token Expirado");
        }catch (JwtException e){
            throw new InvalidTokenException("Token Inválido");

        }
    }

    public boolean isValid(String token){
        try{
            extractClaims(token);
            return true;
        }catch (InvalidTokenException e){
            return false;
        }
    }

    public Long extrackUserId(String token){
        return Long.parseLong(extractClaims(token).getSubject());

    }

    public String extractRole(String token){
        return extractClaims(token).get("role", String.class);
    }
}
