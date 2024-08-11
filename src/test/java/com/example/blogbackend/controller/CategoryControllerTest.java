package com.example.blogbackend.controller;

import com.example.blogbackend.entity.BoardEntity;
import com.example.blogbackend.entity.UserEntity;
import com.example.blogbackend.entity.model.Category;
import com.example.blogbackend.repository.BoardRepository;
import com.example.blogbackend.repository.CategoryRepository;
import com.example.blogbackend.repository.UserRepository;
import com.example.blogbackend.service.BoardService;
import com.example.blogbackend.service.CategoryService;
import com.example.blogbackend.service.UserService;
import com.example.blogbackend.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@Transactional
@SpringBootTest
@ActiveProfiles("test")
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BoardService boardService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WebApplicationContext webApplicationContext;

    private String jwtToken;

    @Autowired
    AuthenticationManager authenticationManager;

    BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    public void setUp() {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(this.webApplicationContext)
                .addFilter(new CharacterEncodingFilter("UTF-8", true))
                .build();

        String encPassword = passwordEncoder.encode("1234");
        UserEntity userEntity = UserEntity.builder()
                .userId("admin01")
                .userName("admin_user")
                .userPw(encPassword)
                .build();
        UserEntity savedUser = userRepository.save(userEntity);
        assertThat(savedUser.getUserId()).isEqualTo(userEntity.getUserId());

        UserDetails user = userService.loadUserByUsername("admin01");
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(user, "1234");
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        jwtToken = jwtUtil.createToken(user, "admin_user");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy:MM:dd HH:mm:ss");

        List<Category> categories = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            Category category = Category.builder()
                    .name("카테고리" + i)
                    .build();
            categories.add(category);
        }
        categoryRepository.saveAll(categories);
        categories.addAll(categoryRepository.findAll());
        for (Category category : categories) {
            System.out.println("추가된 카테고리 리스트 : " + category.getName() + "\n");
        }
        Random random = new Random();
        for (int i = 1; i <= 20; i++) {
            int randomIndex = random.nextInt(categories.size());
            String now = LocalDateTime.now().format(formatter);
            BoardEntity board = BoardEntity.builder()
                    .title("test_title" + i)
                    .contents("test_contents" + i)
                    .author("admin_user")
                    .category(categories.get(randomIndex))
                    .createdAt(LocalDateTime.parse(now, formatter))
                    .build();

            boardRepository.save(board);
        }
    }

    @DisplayName("1. 카테고리 생성 테스트")
    @Test
    public void createCategory() throws Exception{
        ResultActions result = mockMvc.perform(post("/board/list/category")
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.TEXT_PLAIN)
                .content("카테고리10"));

        MvcResult mvcResult = result.andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        List<Category> list = categoryService.getAllCategories();
        for (Category category : list) {
            System.out.println(category.getName());
        }

        assertThat(categoryRepository.findByName("카테고리10")).isPresent();
        System.out.println(mvcResult.getResponse().getContentAsString());
    }

    @DisplayName("2. 카테고리 삭제 테스트")
    @Test
    void deleteCategory() throws Exception{

        ResultActions result = mockMvc.perform(delete("/board/list/category/{id}", "1")
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.TEXT_PLAIN));

        MvcResult mvcResult = result.andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        //assertThat(categoryRepository.findByName("카테고리1")).isEmpty();

        List<Category> list = categoryService.getAllCategories();
        for (Category category : list) {
            System.out.println(category.getName());
        }
    }

    @Test
    void boardList() {
    }

    @Test
    void getBoardsWithCategory() {
    }

    @Test
    void categoryList() {
    }
}