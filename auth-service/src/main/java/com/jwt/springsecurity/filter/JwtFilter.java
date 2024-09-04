package com.jwt.springsecurity.filter;

import com.jwt.springsecurity.exception.InvalidTokenException;
import com.jwt.springsecurity.service.JwtService;
import com.jwt.springsecurity.service.TokenBlacklistService;
import com.jwt.springsecurity.service.UserServiceInfoImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;
    @Autowired
    private TokenBlacklistService tokenBlacklistService;
    @Autowired
    private UserServiceInfoImpl userServiceInfo;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            String username = jwtService.extractUsername(token);
            System.err.println("Inside Filter   --- 1");
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                if (tokenBlacklistService.isTokenBlacklisted(token)) {
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token is blacklisted (Expired)");
                    return;
                }
                System.err.println("Inside Filter   --- 2");
                UserDetails userDetails = userServiceInfo.loadUserByUsername(username);
                System.err.println("Inside Filter   --- 3");
                if (jwtService.validateToken(token, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        }
        filterChain.doFilter(request, response);
    }
}
