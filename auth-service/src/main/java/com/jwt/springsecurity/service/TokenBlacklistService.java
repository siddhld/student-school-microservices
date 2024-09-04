package com.jwt.springsecurity.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TokenBlacklistService {

    @Autowired
    private RedisService redisService;

    @Value("${jwt.access.token.expiry}")
    private long jwtAccessTokenExpiryInMillisecond;

    @Value("${jwt.access.token.expiry.second}")
    private long jwtAccessTokenExpiryInSecond;

    @Transactional(rollbackFor = {Exception.class})
    public void blacklistToken(String token) {
        try {
            // Setting the Expiration time for access token (current time + 15 minutes in millisecond)
            long expirationTime = System.currentTimeMillis() + jwtAccessTokenExpiryInMillisecond;
            redisService.set("access-token-" + token, token, jwtAccessTokenExpiryInSecond);

        } catch (Exception ex) {
            System.err.println(ex.getMessage());
        }
    }

    public boolean isTokenBlacklisted(String token) {

        if (redisService.get("access-token-" + token, String.class) == null) {
            return false;
        }
        return true;
    }
}
