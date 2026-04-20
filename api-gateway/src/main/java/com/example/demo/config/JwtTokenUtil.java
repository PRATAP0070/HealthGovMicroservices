package com.example.demo.config;
 
import java.util.Date;
 
import org.springframework.beans.factory.annotation.Value;

import org.springframework.stereotype.Component;
 
import io.jsonwebtoken.Claims;

import io.jsonwebtoken.Jwts;

import io.jsonwebtoken.security.Keys;
 
@Component

public class JwtTokenUtil {
 
    @Value("${jwt.secret}")

    private String secret;
 
    public Claims getAllClaimsFromToken(String token) {

        return Jwts.parserBuilder()

                .setSigningKey(Keys.hmacShaKeyFor(secret.getBytes()))

                .build()

                .parseClaimsJws(token)

                .getBody();

    }
 
    public boolean isTokenValid(String token) {

        Claims claims = getAllClaimsFromToken(token);

        return claims.getExpiration().after(new Date());

    }

}

 