package com.example.blogbackend.service;

import com.example.blogbackend.dto.BoardDto;
import com.example.blogbackend.entity.BoardEntity;
import com.example.blogbackend.entity.model.Category;
import com.example.blogbackend.entity.model.SearchCondition;
import com.example.blogbackend.exception.BoardNotFoundException;
import com.example.blogbackend.entity.model.Header;
import com.example.blogbackend.entity.model.Pagination;
import com.example.blogbackend.repository.BoardRepository;
import com.example.blogbackend.repository.BoardRepositoryCustom;
import com.example.blogbackend.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class BoardService {

    private final BoardRepository boardRepository;
    private final BoardRepositoryCustom boardRepositoryCustom;
    private final CategoryRepository categoryRepository;

    /**
     * 게시글 목록 가져오기
     */
    public Header<List<BoardDto>> getBoardList(Pageable pageable, SearchCondition searchCondition) {
        List<BoardDto> dtos = new ArrayList<>();

        Page<BoardEntity> boardList = boardRepositoryCustom.findAllBySearchCondition(pageable, searchCondition);
        for (BoardEntity boardEntity : boardList) {
            BoardDto dto = BoardDto.builder()
                    .idx(boardEntity.getIdx())
                    .author(boardEntity.getAuthor())
                    .title(boardEntity.getTitle())
                    .contents(boardEntity.getContents())
                    .category(boardEntity.getCategory())
                    .createdAt(boardEntity.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss")))
                    .build();

            dtos.add(dto);
        }

        Pagination pagination = new Pagination(
                (int) boardList.getTotalElements(),
                pageable.getPageNumber() + 1,
                pageable.getPageSize(),
                10
        );

        return Header.OK(dtos, pagination);
    }



    /**
     * 게시글 가져오기
     */
    public BoardDto getBoard(Long id) {
        BoardEntity boardEntity = boardRepository.findById(id).orElseThrow(BoardNotFoundException::new);
        return BoardDto.builder()
                .idx(boardEntity.getIdx())
                .title(boardEntity.getTitle())
                .author(boardEntity.getAuthor())
                .contents(boardEntity.getContents())
                .createdAt(boardEntity.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss")))
                .build();
    }

    /**
     * 게시글 등록
     */
    public BoardEntity create(BoardDto boardDto) throws IllegalAccessException {
        if (boardDto.getAuthor() == null) {
            throw new IllegalAccessException("로그인 후 이용이 가능합니다.");
        }

        Category category = categoryRepository.findById(boardDto.getCategoryId()).orElseThrow();

        BoardEntity boardEntity = BoardEntity.builder()
                .title(boardDto.getTitle())
                .author(boardDto.getAuthor())
                .contents(boardDto.getContents())
                .category(category)
                .createdAt(LocalDateTime.now())
                .build();

        return boardRepository.save(boardEntity);
    }

    /**
     * 게시글 수정
     */
    public BoardEntity update(BoardDto boardDto) {
        Category category = categoryRepository.findById(boardDto.getCategoryId()).orElseThrow();

        BoardEntity boardEntity = boardRepository.findById(boardDto.getIdx()).orElseThrow(BoardNotFoundException::new);
        boardEntity.setTitle(boardDto.getTitle());
        boardEntity.setContents(boardDto.getContents());
        boardEntity.setCategory(category);
        return boardRepository.save(boardEntity);
    }

    /**
     * 게시글 삭제
     */
    public void delete(Long id) {
        BoardEntity boardEntity = boardRepository.findById(id).orElseThrow(BoardNotFoundException::new);
        boardRepository.delete(boardEntity);
    }

    /**
     * 카테고리별로 게시글 갯수 가져오기
     */
    public Map<String, Long> getBoardsCountByCategory() {
        List<Object[]> results = boardRepository.countBoardsByCategory();

        Map<String, Long> postCountByCategory = new HashMap<>();
        for (Object[] result : results) {
            String categoryName = (String) result[0];
            Long postCount = (Long) result[1];
            postCountByCategory.put(categoryName, postCount);
        }

        return postCountByCategory;
    }

    /**
     * 모든 게시글 카운팅
     */
    public String getTotalBoards() {
        return String.valueOf(boardRepository.countTotalBoards());
    }

    public BoardEntity findById(Long id) {
        return boardRepository.findById(id).orElseThrow(BoardNotFoundException::new);
    }
}
