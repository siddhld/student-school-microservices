package com.jwt.springsecurity.controller;

import com.jwt.springsecurity.exception.UserAlreadyExistException;
import com.jwt.springsecurity.model.UserInfo;
import com.jwt.springsecurity.service.JwtService;
import com.jwt.springsecurity.service.TokenBlacklistService;
import com.jwt.springsecurity.service.TokenService;
import com.jwt.springsecurity.service.UserServiceInfoImpl;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private TokenService tokenService;
    @Autowired
    private UserServiceInfoImpl userService;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private TokenBlacklistService tokenBlacklistService;

    @GetMapping("/test")
    public String welcome() {
        return "Api is working fine";
    }

    @GetMapping("/validate-token")
    public boolean validateToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return false; // If no authorization header or it doesn't start with "Bearer ", return false
        }
        String token = authHeader.substring(7); // Remove "Bearer " from the token

        try {
            // Attempt to validate the token
            if (jwtService.validateToken(token)) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            // Catch any exceptions that occur during token validation (e.g., SignatureException)
            System.err.println("Token validation failed: " + e.getMessage());
            return false; // Return false if token validation fails
        }
    }

    @PostMapping("/login")
    public String login(@RequestBody UserInfo userInfo) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userInfo.getUsername(), userInfo.getPassword()));
        if (authentication.isAuthenticated()) {
            userInfo = (UserInfo) userService.loadUserByUsername(userInfo.getUsername());
            String token = jwtService.generateToken(userInfo);
            String refreshToken = jwtService.generateRefreshToken(userInfo);
            tokenService.saveRefreshToken(userInfo.getUsername(), refreshToken);

            // Setting the user details in Security Context holder
            SecurityContextHolder.getContext().setAuthentication(authentication);
            return "Access Token: " + token + "\nRefresh Token: " + refreshToken;
        } else {
            throw new UsernameNotFoundException("Invalid user request");
        }
    }

    @PostMapping("/signup")
    public String addUser(@RequestBody UserInfo userInfo) {

        UserDetails userDetails = userService.loadUserByUsername(userInfo.getUsername());

        if (userDetails == null) {
            userService.addUser(userInfo);

            String token = jwtService.generateToken(userInfo);
            String refreshToken = jwtService.generateRefreshToken(userInfo);

            tokenService.saveRefreshToken(userInfo.getUsername(), refreshToken);
            return "Access Token: " + token + "\nRefresh Token: " + refreshToken;
        } else {
            throw new UserAlreadyExistException("User is already Registered");
        }
    }
    @PostMapping("/logout")
//    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            String username = jwtService.extractUsername(token);
            tokenBlacklistService.blacklistToken(token);
            tokenService.clearRefreshToken(username);
            return ResponseEntity.ok("Logged out successfully");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid request");
        }
    }

    @PostMapping("/generate-access-token")
    public ResponseEntity<String> generateAccessToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Missing or invalid Authorization header");
        }
        String refreshToken = authHeader.substring(7);

        if (jwtService.validateRefreshToken(refreshToken)) {
            String username = jwtService.extractUsername(refreshToken);
            UserInfo userInfo = (UserInfo) userService.loadUserByUsername(username);
            String newAccessToken = jwtService.generateToken(userInfo);
            return ResponseEntity.ok("Access Token: " + newAccessToken);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid refresh token");
        }
    }

}
