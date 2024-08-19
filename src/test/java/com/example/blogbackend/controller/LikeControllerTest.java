package com.example.blogbackend.controller;

import com.example.blogbackend.dto.LikeDto;
import com.example.blogbackend.entity.BoardEntity;
import com.example.blogbackend.entity.Like;
import com.example.blogbackend.entity.UserEntity;
import com.example.blogbackend.exception.AlreadyLikedException;
import com.example.blogbackend.repository.LikeRepository;
import com.example.blogbackend.service.BoardService;
import com.example.blogbackend.service.LikeService;
import com.example.blogbackend.service.UserService;
import com.example.blogbackend.util.JwtUtil;
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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@Transactional
@SpringBootTest
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestPropertySource(properties = {
        "jwt.secret=AHEFIOENAFLDFHEUFHOOOEAFHHFAHEILHFAIEH@!@#HN!#LH!@",
        "jwt.expiration=3600"
})
class LikeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BoardService boardService;

    @MockBean
    private UserService userService;

    @MockBean
    private LikeService likeService;

    @MockBean
    private LikeRepository likeRepository;

    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private WebApplicationContext webApplicationContext;

    private UserEntity user;
    private BoardEntity board;
    private Like like;
    private String jwtToken;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(this.webApplicationContext)
                .build();
        user = new UserEntity();
        user.setIdx(1L);
        user.setUserName("admin_user");
        user.setUserId("admin01");
        user.setUserPw("1234");

        board = new BoardEntity();
        board.setIdx(1L);
        board.setTitle("Test Title");
        board.setContents("Board Test Content");
        board.setAuthor("admin01");
        board.setCreatedAt(LocalDateTime.parse("2024-08-05T17:15:32", DateTimeFormatter.ISO_LOCAL_DATE_TIME));

        when(userService.findByIdx(1L)).thenReturn(user);
        when(boardService.findById(1L)).thenReturn(board);

        UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                .username(user.getUserId())
                .password(user.getUserPw())
                .roles("ADMIN")
                .build();

        when(userService.loadUserByUsername("admin01")).thenReturn(userDetails);
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(user, "1234");
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        jwtToken = jwtUtil.createToken(userDetails, user.getUserName());
    }

    @DisplayName("1. 좋아요 생성")
    @Test
    void testInsert() throws Exception {
        LikeDto likeDto = LikeDto.builder()
                .userId(user.getIdx())
                .boardId(board.getIdx())
                .build();

        doNothing().when(likeService).insert(any(LikeDto.class));

        ResultActions result = mockMvc.perform(post("/board/like")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new Gson().toJson(likeDto)));

        result
            .andExpect(status().isOk())
            .andDo(print())
            .andReturn();
    }

    @DisplayName("2. 이미 눌러진 좋아요에 다시 좋아요 요청시 AlreadyLikedException 실행 확인")
    @Test
    void testInsertAlreadyLikedException() throws Exception {
        LikeDto likeDto = LikeDto.builder()
                .userId(user.getIdx())
                .boardId(board.getIdx())
                .build();

        Like existingLike = Like.builder()
                .userEntity(user)
                .boardEntity(board)
                .build();

        when(likeRepository.findByUserEntityAndBoardEntity(any(UserEntity.class), any(BoardEntity.class)))
                .thenReturn(Optional.of(existingLike));

        doThrow(new AlreadyLikedException()).when(likeService).insert(any(LikeDto.class));

        mockMvc.perform(post("/board/like")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new Gson().toJson(likeDto)))
                .andExpect(status().isConflict())
                .andDo(print())
                .andReturn();
    }

    @DisplayName("3. 좋아요 취소 테스트")
    @Test
    void testDelete() throws Exception {
        // Given
        LikeDto likeDto = LikeDto.builder()
                .userId(user.getIdx()) // 테스트에 사용할 사용자 ID
                .boardId(board.getIdx()) // 테스트에 사용할 게시글 ID
                .build();

        // likeService의 delete 메서드가 호출될 때 아무 동작도 하지 않도록 설정
        doNothing().when(likeService).delete(any(LikeDto.class));

        // When
        ResultActions result = mockMvc.perform(delete("/board/like")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new Gson().toJson(likeDto)));

        // Then
        result.andExpect(status().isOk()) // 상태 코드가 200인지 확인
                .andDo(print()) // 요청과 응답 내용을 출력
                .andReturn(); // 결과 반환

        // likeService의 delete 메서드가 한 번 호출되었는지 확인
        verify(likeService, times(1)).delete(any(LikeDto.class));
    }
}