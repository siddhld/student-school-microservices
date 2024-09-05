package com.jwt.springsecurity.service;

import com.jwt.springsecurity.exception.NotAuthorizeException;
import com.jwt.springsecurity.model.UserInfo;
import com.jwt.springsecurity.repository.UserInfoRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceInfoImpl implements UserDetailsService {

    @Autowired
    private UserInfoRepo repo;

    @Autowired
    @Lazy
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username){
        Optional<UserInfo> info = repo.findByUsername(username);
        UserDetails userDetails = info.orElse(null);
        return userDetails;
    }

    public UserInfo addUser(UserInfo info){
        info.setPassword(passwordEncoder.encode(info.getPassword()));
        return repo.save(info);
    }

}
