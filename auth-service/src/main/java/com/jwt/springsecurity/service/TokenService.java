package com.jwt.springsecurity.service;

import com.jwt.springsecurity.model.UserInfo;
import com.jwt.springsecurity.repository.UserInfoRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TokenService {

    @Autowired
    private UserServiceInfoImpl userService;
    @Autowired
    private UserInfoRepo repo;

    // Methods for refresh tokens
    public void saveRefreshToken(String username, String refreshToken) {
        UserInfo user = (UserInfo) userService.loadUserByUsername(username);
        user.setRefreshToken(refreshToken);
        repo.save(user);
    }

    public String getRefreshToken(String username) {
        UserInfo user = (UserInfo) userService.loadUserByUsername(username);
        return user.getRefreshToken();
    }

    public void clearRefreshToken(String username) {
        UserInfo user = (UserInfo) userService.loadUserByUsername(username);
        user.setRefreshToken(null);
        repo.save(user);
    }

}
