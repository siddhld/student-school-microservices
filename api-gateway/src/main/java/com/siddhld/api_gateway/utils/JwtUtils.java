package com.siddhld.api_gateway.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.List;

@Component
public class JwtUtils {

    @Value("${jwt.secret.key}")
    private String SECRET;

    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSignInKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (SignatureException e) {
            System.err.println("Invalid JWT signature: " + e.getMessage());
            return null; // Return null or handle in a better way
        } catch (ExpiredJwtException e) {
            System.err.println("JWT expired: " + e.getMessage());
            return null; // Return null for expired tokens
        } catch (MalformedJwtException e) {
            System.err.println("Invalid JWT format: " + e.getMessage());
            return null; // Handle malformed token
        } catch (Exception e) {
            System.err.println("Error parsing JWT: " + e.getMessage());
            return null;
        }
    }

    public boolean validateToken(String token) {
        try {
            final Claims claims = extractAllClaims(token);
            if (claims == null) {
                return false; // Invalid or expired token
            }
            return true;
        } catch (Exception e) {
            System.err.println("Token validation failed: " + e.getMessage());
            return false;
        }
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET);
        return Keys.hmacShaKeyFor(keyBytes);
    }

}
