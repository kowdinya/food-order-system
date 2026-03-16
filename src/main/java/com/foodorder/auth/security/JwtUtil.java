package com.foodorder.auth.security;

import com.foodorder.auth.model.Role;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;


@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiry}")
    private long expiration;

    public String generateToken(String email, Role role){
        return Jwts.builder()
                .setSubject(email)
                .claim("role",role.name())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis()+expiration))
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes()))//sign it
                .compact();//convert to string

    }
    public String extractEmail(String token){

        return Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(secretKey.getBytes()))
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();//this is the email we get as subject

    }

    public boolean isTokenValid(String token){

        try{
            Date expiry = Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(secretKey.getBytes()))
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getExpiration();
            return expiry.after(new Date());
        }
        catch(Exception e){
            return false;//token is invalid
        }
    }

}
