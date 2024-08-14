package com.example.blogbackend.controller;

import com.example.blogbackend.dto.CommentDto;
import com.example.blogbackend.entity.BoardEntity;
import com.example.blogbackend.entity.Comment;
import com.example.blogbackend.entity.UserEntity;
import com.example.blogbackend.entity.model.Category;
import com.example.blogbackend.repository.BoardRepository;
import com.example.blogbackend.repository.CategoryRepository;
import com.example.blogbackend.repository.CommentRepository;
import com.example.blogbackend.repository.UserRepository;
import com.example.blogbackend.service.BoardService;
import com.example.blogbackend.service.CategoryService;
import com.example.blogbackend.service.CommentService;
import com.example.blogbackend.service.UserService;
import com.example.blogbackend.util.JwtUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@Transactional
@SpringBootTest
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BoardService boardService;
    @MockBean
    private CategoryService categoryService;
    @MockBean
    private UserService userService;
    @MockBean
    private CommentService commentService;

    @Mock
    private ObjectMapper objectMapper;

    private UserEntity user;
    private BoardEntity board;
    private Category category;
    private Comment comment;

    @BeforeEach
    void setUp() {
        user = new UserEntity();
        user.setIdx(1L);
        user.setUserName("admin_user");
        user.setUserId("admin01");
        user.setUserPw("1234");

        category = new Category();
        category.setId(1L);
        category.setName("Test Category");

        board = new BoardEntity();
        board.setIdx(1L);
        board.setTitle("Test Title");
        board.setContents("Board Test Content");
        board.setAuthor("admin01");
        board.setCategory(category);
        board.setCreatedAt(LocalDateTime.parse("2024-08-05T17:15:32", DateTimeFormatter.ISO_LOCAL_DATE_TIME));

        when(userService.findByIdx(1L)).thenReturn(user);
        when(categoryService.findById(1L)).thenReturn(category);
        when(boardService.findById(1L)).thenReturn(board);

        comment = new Comment();
        comment.setId(1L);
        comment.setContent("Comment Test Content");
        comment.setAuthor("admin01");
        comment.setCreatedAt(LocalDateTime.parse("2024-08-05T17:15:32", DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        comment.setBoardEntity(board);
    }

    @DisplayName("1. 댓글 생성 테스트")
    @Test
    void createComment() throws Exception{
        CommentDto commentDto = CommentDto.builder()
                .id(comment.getId())
                .boardId(comment.getBoardEntity().getIdx())
                .content(comment.getContent())
                .author(comment.getAuthor())
                .createdAt(comment.getCreatedAt().toString())
                .build();

        when(commentService.createComment(any(CommentDto.class)))
                .thenReturn(comment);

        ResultActions result = mockMvc.perform(post("/comment")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new Gson().toJson(commentDto)));

        MvcResult mvcResult = result
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        System.out.println("mvcResult : " + mvcResult.getResponse().getContentAsString());
    }

    @Test
    void getCommentsByBoard() throws Exception {
        // Step 1: Create a mock post and associated comments
        BoardEntity mockBoard = BoardEntity.builder()
                .idx(1L)
                .title("Test Post")
                .author("admin01")
                .contents("Test Contents")
                .createdAt(LocalDateTime.now())
                .build();

        List<CommentDto> commentList = List.of(
                CommentDto.builder().id(1L).boardId(mockBoard.getIdx()).author("user1").content("Comment 1").createdAt(LocalDateTime.now().toString()).build(),
                CommentDto.builder().id(2L).boardId(mockBoard.getIdx()).author("user2").content("Comment 2").createdAt(LocalDateTime.now().toString()).build()
        );

        // Step 2: Mock the service to return the comment list
        when(commentService.getCommentsByBoard(any(Long.class))).thenReturn(commentList);

        // Step 3: Perform the request using MockMvc
        ResultActions result = mockMvc.perform(get("/comment/board/{boardId}", 1L)
                .contentType(MediaType.APPLICATION_JSON));

        // Step 4: Verify the results
        MvcResult mvcResult = result.andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        // Convert the response JSON to a List of CommentDto objects
        String responseContent = mvcResult.getResponse().getContentAsString();
        List<CommentDto> returnedComments = new ObjectMapper().readValue(responseContent, new TypeReference<List<CommentDto>>() {});

        // Use assertions to verify the response
        assertEquals(2, returnedComments.size());
        assertEquals("Comment 1", returnedComments.get(0).getContent());
        assertEquals("Comment 2", returnedComments.get(1).getContent());
    }
}