package com.example.blogbackend.controller;

import com.example.blogbackend.dto.BoardDto;
import com.example.blogbackend.entity.BoardEntity;
import com.example.blogbackend.entity.UserEntity;
import com.example.blogbackend.repository.BoardRepository;
import com.example.blogbackend.repository.BoardRepositoryCustom;
import com.example.blogbackend.repository.UserRepository;
import com.example.blogbackend.service.BoardService;
import com.example.blogbackend.service.UserService;
import com.example.blogbackend.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@Transactional
@SpringBootTest
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class BoardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BoardService boardService;

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private BoardRepositoryCustom boardRepositoryCustom;

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
        for (int i = 1; i <= 200; i ++) {
            String now = LocalDateTime.now().format(formatter);
            BoardEntity board = BoardEntity.builder()
                    .title("test_title" + i)
                    .contents("test_contents" + i)
                    .author("admin_user")
                    .createdAt(LocalDateTime.parse(now, formatter))
                    .build();

            boardRepository.save(board);
        }
    }

    @DisplayName("1. 게시글 생성 테스트")
    @Order(1)
    @Test
    public void createBoard() throws Exception {
        BoardDto boardDto = BoardDto.builder()
                .idx(300L)
                .title("Test Title")
                .contents("Test Contents")
                .author("admin_user")
                .createdAt("2024-08-05T17:15:32")
                .build();

        BoardEntity board = new BoardEntity();
        board.setIdx(300L);
        board.setTitle("Test Title");
        board.setContents("Test Contents");
        board.setAuthor("admin_user");
        board.setCreatedAt(LocalDateTime.parse("2024-08-05T17:15:32", DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        when(boardService.create(any(BoardDto.class)))
                .thenReturn(board);

        ResultActions result = mockMvc.perform(post("/board")
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(boardDto)));

        MvcResult mvcResult = result.andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        System.out.println(mvcResult.getResponse().getContentAsString());

        // ArgumentCaptor를 사용하여 전달된 인수 확인
        ArgumentCaptor<BoardDto> captor = ArgumentCaptor.forClass(BoardDto.class);
        verify(boardService).create(captor.capture());
        BoardDto capturedArgument = captor.getValue();
        assertThat(capturedArgument.getTitle()).isEqualTo("Test Title");
        assertThat(capturedArgument.getContents()).isEqualTo("Test Contents");
    }

    @Test
    @DisplayName("2. 게시글 리스트 조회 테스트")
    @Order(2)
    public void boardList() throws Exception {
        ResultActions result = mockMvc.perform(get("/board/list")
                        .header("Authorization", "Bearer " + jwtToken)
                        .param("sk", "")
                        .param("sv", "")
                        .param("page", "0")
                        .param("size", "10"));

        MvcResult mvcResult = result.andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        System.out.println("==============\n" +
                mvcResult.getResponse().getContentAsString()
        + "\n=====================");

        // You can add additional assertions here to verify the response content
    }

    @DisplayName("3. 게시글 수정 테스트")
    @Order(3)
    @Test
    public void updateBoard() throws Exception {
        BoardDto updatedBoardDto = BoardDto.builder()
                .idx(1L)
                .title("Updated Title")
                .contents("Updated Contents")
                .author("admin_user")
                .createdAt("2024-08-05T17:15:32")
                .build();

        BoardEntity updatedBoardEntity = new BoardEntity();
        updatedBoardEntity.setIdx(1L);
        updatedBoardEntity.setTitle("Updated Title");
        updatedBoardEntity.setContents("Updated Contents");
        updatedBoardEntity.setAuthor("admin_user");
        updatedBoardEntity.setCreatedAt(LocalDateTime.parse("2024-08-05T17:15:32", DateTimeFormatter.ISO_LOCAL_DATE_TIME));

        when(boardService.update(any(BoardDto.class)))
                .thenReturn(updatedBoardEntity);

        ResultActions result = mockMvc.perform(patch("/board")
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedBoardDto)));

        MvcResult mvcResult = result.andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        System.out.println(mvcResult.getResponse().getContentAsString());
    }

    @DisplayName("4. 게시글 하나 가져오기")
    @Order(4)
    @Test
    public void getBoard() throws Exception {
        ResultActions result = mockMvc.perform(get("/board/{id}", 5L)
                .header("Authorization", "Bearer " + jwtToken));

        MvcResult mvcResult = result.andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        System.out.println(mvcResult.getResponse().getContentAsString());
    }

    @DisplayName("5. 게시글 지우기")
    @Order(5)
    @Test
    public void deleteBoard() throws Exception {
        ResultActions result = mockMvc.perform(delete("/board/{id}", 1L)
                        .header("Authorization", "Bearer " + jwtToken))
                .andDo(print())
                .andExpect(status().isOk());

        MvcResult mvcResult = result.andReturn();

        System.out.println(mvcResult.getResponse().getContentAsString());
    }
}