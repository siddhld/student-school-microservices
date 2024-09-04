package com.jwt.springsecurity.service;

import com.jwt.springsecurity.model.UserInfo;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
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
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public List<GrantedAuthority> extractAuthorities(String token) {

        List<Map<String, String>> authorities = extractClaim(token, claims -> claims.get("authorities", List.class));
        List<String> roles = authorities.stream()
                .map(authorityMap -> authorityMap.get("authority"))
                .collect(Collectors.toList());

        return roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
        final Claims claim = extractAllClaims(token);
        return claimResolver.apply(claim);
    }

    private Claims extractAllClaims(String token) {

        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims;
    }


    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);

        try {
            extractAllClaims(token);
            return (username.equals(userDetails.getUsername()) && !isTokenExpired(token) && !tokenBlacklistService.isTokenBlacklisted(token));
        } catch (Exception e) {
            return false;
        }
    }

    public boolean validateToken(String token) {
        try {
            System.err.println("Inside Validate method   --- 1");
            extractAllClaims(token);
            System.err.println("Inside Validate method   --- 2");
            return (!isTokenExpired(token) && !tokenBlacklistService.isTokenBlacklisted(token));
        } catch (Exception e) {
            System.err.println("Inside Validate method   --- 3");
            return false;
        }
    }

//    The validateRefreshToken method is designed to check if a given JWT refresh token is valid.
//    The validity is determined by whether the token can be parsed and its claims can be extracted without
//    throwing an exception.
    public boolean validateRefreshToken(String token) {
        try {
            extractAllClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}
