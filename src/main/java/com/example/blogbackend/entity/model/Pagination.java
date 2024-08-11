package com.example.blogbackend.entity.model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Pagination {

    // 페이지당 보여지는 게시글 최대 개수
    private int pageSize;

    // 현재 페이지
    int page;

    // 현재 블럭
    int block;

    // 총 게시글 수
    int totalListCnt;

    // 총 페이지 수
    int totalPageCnt;

    // 총 구간 수
    int totalBlockCnt;

    // 시작 페이지
    int startPage;

    // 마지막 페이지
    int endPage;

    // 이전 구간 마지막 페이지
    int prevBlock;

    // 다음 구간 시작 페이지
    int nextBlock;

    // 인덱스
    int startIndex;

    public Pagination(Integer totalListCnt, Integer page, Integer pageSize, Integer blockSize) {
        this.pageSize = pageSize;
        this.page = page;
        this.totalListCnt = totalListCnt;
        totalPageCnt = (int) Math.ceil(totalListCnt * 1.0 / this.pageSize);
        totalBlockCnt = (int) Math.ceil(totalPageCnt * 1.0 / blockSize);
        block = (int) Math.ceil((this.page * 1.0) / blockSize);
        // if(block < 1) block = 1
        startPage = ((block - 1) * blockSize + 1);
        endPage = startPage + blockSize - 1;
        // 블럭 마지막 페이지 validation
        if (endPage > totalPageCnt) endPage = totalPageCnt;
        // 이전 블럭 (클릭 시, 이전 블럭 마지막 페이지)
        prevBlock = (block * blockSize) - blockSize;
        // 이전 블럭 validation
        if (prevBlock < 1) prevBlock = 1;
        // 다음 블럭(클릭 시, 다음 블럭 첫 번째 페이지)
        nextBlock = (block * blockSize + 1);
        // 다음 블럭 validation
        if (nextBlock > totalPageCnt) nextBlock = totalPageCnt;
        // if(this.page < 1) this.page = 1
        startIndex = (this.page - 1) * this.pageSize;
    }
}
