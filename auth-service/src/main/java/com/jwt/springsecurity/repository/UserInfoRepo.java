package com.jwt.springsecurity.repository;

import com.jwt.springsecurity.model.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserInfoRepo extends JpaRepository<UserInfo, Integer> {
    public Optional<UserInfo> findByUsername(String username);

}
