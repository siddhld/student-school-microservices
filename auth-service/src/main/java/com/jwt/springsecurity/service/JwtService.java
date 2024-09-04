package com.jwt.springsecurity.service;

import com.jwt.springsecurity.model.UserInfo;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class JwtService {
    private static Map<String, Object> header = new HashMap<>();
    private UserServiceInfoImpl userService;
    @Autowired
    private TokenBlacklistService tokenBlacklistService;

    @Value("${jwt.secret.key}")
    private String SECRET;
    @Value("${jwt.access.token.expiry}")
    private long jwtAccessTokenExpiry;
    @Value("${jwt.refresh.token.expiry}")
    private long jwtRefreshTokenExpiry;

    public String generateToken(UserInfo userInfo) {
        // Constructing Token
        header.put("alg", "HS256");
        header.put("typ", "JWT");
        UserDetails ud = new UserInfo();

        // If user is "Signing Up" or "Logging In" then add "user's authorities" in token.
        Map<String, Object> claims = (userInfo.getRoles() != null && !userInfo.getRoles().isEmpty()) ? setAuthorities(userInfo) : new HashMap();

        // Constructing Token
        return Jwts.builder()
                .setHeader(header)
                .setClaims(claims)
                .setSubject(userInfo.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtAccessTokenExpiry)) // 15 minutes
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateRefreshToken(UserInfo userInfo) {
        header.put("alg", "HS256");
        header.put("typ", "JWT");
        return Jwts.builder()
                .setHeader(header)
                .setSubject(userInfo.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtRefreshTokenExpiry)) // 7 days
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private Map<String, Object> setAuthorities(UserInfo userInfo) {

        Map<String, Object> claims = new HashMap<>();
        claims.put("authorities", userInfo.getAuthorities());
        return claims;
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String extractUsername(String token) {
        String username = extractClaim(token, Claims::getSubject);
        if (username == null) {
            return null; // Handle null username case
        }
        return username;
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
        final Claims claim = extractAllClaims(token);
        if (claim == null) {
            // Return null or handle accordingly when claims are null
            return null;
        }
        return claimResolver.apply(claim);
    }

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


    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);

        try {
            final Claims claims = extractAllClaims(token);
            if (claims == null) {
                return false; // Invalid or expired token
            }
            return (username.equals(userDetails.getUsername()) && !isTokenExpired(token) && !tokenBlacklistService.isTokenBlacklisted(token));
        } catch (Exception e) {
            return false;
        }
    }

    public boolean validateToken(String token) {
        try {
            final Claims claims = extractAllClaims(token);
            if (claims == null) {
                return false; // Invalid or expired token
            }
            return (!isTokenExpired(token) && !tokenBlacklistService.isTokenBlacklisted(token));
        } catch (Exception e) {
            System.err.println("Token validation failed: " + e.getMessage());
            return false;
        }
    }

//    The validateRefreshToken method is designed to check if a given JWT refresh token is valid.
//    The validity is determined by whether the token can be parsed and its claims can be extracted without
//    throwing an exception.
    public boolean validateRefreshToken(String token) {
        try {
            final Claims claims = extractAllClaims(token);
            if (claims == null) {
                return false; // Invalid or expired token
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}
