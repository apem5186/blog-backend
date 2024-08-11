package com.example.blogbackend.controller;

import com.example.blogbackend.entity.UserEntity;
import com.example.blogbackend.repository.UserRepository;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.awt.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class UserControllerTest {

    @Autowired
    UserController userController;

    @Autowired
    UserRepository userRepository;

    @Autowired
    BCryptPasswordEncoder passwordEncoder;

    private MockMvc mockMvc;

//    @DisplayName("초기 셋팅 유저 데이터 생성하기")
//    @BeforeEach
//    public void init() {
//        String encPassword = passwordEncoder.encode("test_password");
//        UserEntity userEntity = UserEntity.builder()
//                .userId("test_user")
//                .userPw(encPassword)
//                .userName("테스트유저")
//                .email("user01@email.com")
//                .build();
//
//        UserEntity savedUser = userRepository.save(userEntity);
//        assertThat(userEntity.getUserId()).isEqualTo(savedUser.getUserId());
//    }

    @DisplayName("1. 유저 데이터 생성하기")
    @Test
    void test_0() {
        String encPassword = passwordEncoder.encode("test_password");
        UserEntity userEntity = UserEntity.builder()
                .userId("test_user")
                .userPw(encPassword)
                .userName("테스트유저")
                .build();

        UserEntity savedUser = userRepository.save(userEntity);
        assertThat(userEntity.getUserId()).isEqualTo(savedUser.getUserId());
    }

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @DisplayName("1. 로그인 실패 테스트")
    @Test
    void test_1() throws Exception {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("user_id", "test_userr");
        jsonObject.put("user_pw", "test_passwordd");

        ResultActions result = mockMvc.perform(post("/user/login")
                .content(jsonObject.toString())
                .contentType(MediaType.APPLICATION_JSON));

        MvcResult mvcResult = result.andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        System.out.println(mvcResult.getResponse().getContentAsString());
    }

    @DisplayName("2. 로그인 성공 테스트")
    @Test
    void test_2() throws Exception {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("user_id", "test_user");
        jsonObject.put("user_pw", "test_password");

        ResultActions result = mockMvc.perform(post("/user/login")
                .content(jsonObject.toString())
                .contentType(MediaType.APPLICATION_JSON));

        MvcResult mvcResult = result.andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        System.out.println(mvcResult.getResponse().getContentAsString());
    }
}