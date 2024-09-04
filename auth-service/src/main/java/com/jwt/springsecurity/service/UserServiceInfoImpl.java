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

    // This will Fetch only those Users which has Authority matches in the Token's Authority
    public UserInfo getById(Integer id){

        // Fetching authenticated user
        UserInfo authenticatedUserInfo = getAuthenticatedUser();
        if (authenticatedUserInfo == null) {
            return null;
        }

        // Fetching DB Table user
        Optional<UserInfo> dbUser = repo.findById(id);
        if (dbUser.isEmpty()) {
            return null;
        }

        UserInfo userInfo = dbUser.get();
        if (isUserAuthorized(authenticatedUserInfo, userInfo)) {
            return userInfo;
        } else {
            throw new NotAuthorizeException(authenticatedUserInfo.getUsername()+" cannot access "+userInfo.getRoles().get(0)+"'s Data");
        }
    }

    public List<UserInfo> getAll(){
        return repo.findAll();
    }

    private UserInfo getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserInfo) {
            return (UserInfo) authentication.getPrincipal();
        }
        return null;
    }

    private boolean isUserAuthorized(UserInfo authenticatedUser, UserInfo targetUser) {
        List<GrantedAuthority> authenticatedUserRoles = (List<GrantedAuthority>) authenticatedUser.getAuthorities();
        List<GrantedAuthority> targetUserRoles = (List<GrantedAuthority>) targetUser.getAuthorities();

        boolean isAdmin = authenticatedUserRoles.contains(new SimpleGrantedAuthority("ADMIN"));
        boolean hasCommonRole = authenticatedUserRoles.stream()
                .anyMatch(role -> targetUserRoles.contains(role));

        return isAdmin || hasCommonRole;
    }

}
