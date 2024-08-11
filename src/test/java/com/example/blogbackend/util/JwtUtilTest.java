package com.example.blogbackend.util;

import com.example.blogbackend.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class JwtUtilTest {

    @Autowired
    JwtUtil jwtUtil;

    @Autowired
    UserService userService;

    @DisplayName("1. 토큰 생성 후 검증")
    @Test
    void test_1() {
        String userId = "user1";
        String userName = "사용자1";

        UserDetails loginUser = userService.loadUserByUsername(userId);

        String token = jwtUtil.createToken(loginUser, userName);

        System.out.println("Token : " + token);

        assert(jwtUtil.decodeToken(token).getClaim("userName").asString().equals(userName));
    }

}