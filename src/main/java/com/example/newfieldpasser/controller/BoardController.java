package com.example.newfieldpasser.controller;

import com.example.newfieldpasser.dto.BoardDTO;
import com.example.newfieldpasser.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    /*
    게시글 등록
     */
    @PostMapping("/board/register")
    public ResponseEntity<?> registerBoard(@RequestParam("file") MultipartFile file,
                                           Authentication authentication,
                                           BoardDTO.boardReqDTO boardReqDTO) {

        return boardService.registerBoard(file, authentication, boardReqDTO);
    }

    /*
    게시글 상세조회
     */
    @GetMapping("/board/{boardId}")
    public ResponseEntity<?> boardInquiryDetail(@PathVariable long boardId) {
        // 상세조회 시 조회 수 카운트
        boardService.updateViewCount(boardId);

        return boardService.boardInquiryDetail(boardId);
    }

    /*
    게시글 수정
     */
    @PutMapping("/board/edit/{boardId}")
    public ResponseEntity<?> editBoard(@PathVariable long boardId,
                                       @RequestParam("file") MultipartFile file,
                                       BoardDTO.boardReqDTO boardReqDTO) {

        return boardService.editBoard(boardId,file,boardReqDTO);
    }

    /*
    게시글 삭제
     */
    @DeleteMapping("/board/delete/{boardId}")
    public ResponseEntity<?> deleteBoard(@PathVariable long boardId) {

        return boardService.deleteBoard(boardId);
    }

    /*
    게시글 리스트 조회
     */
    @GetMapping("/board/list/{page}")
    public ResponseEntity<?> boardListInquiry(@PathVariable int page) {

        return boardService.boardListInquiry(page);
    }

    /*
    게시글 리스트 조회 - 카테고리별
     */
    @GetMapping("/board/list/category/{categoryId}/{page}")
    public ResponseEntity<?> boardListInquiryByCategory(@PathVariable int categoryId,
                                                        @PathVariable int page) {

        return boardService.boardListInquiryByCategory(categoryId, page);
    }

    /*
    게시글 리스트 조회 - 지역별
     */
    @GetMapping("/board/list/district/{districtId}/{page}")
    public ResponseEntity<?> boardListInquiryByDistrict(@PathVariable int districtId,
                                                        @PathVariable int page) {

        return boardService.boardListInquiryByDistrict(districtId, page);
    }

    /*
    게시글 리스트 조회 - 카테고리 + 지역
     */
    @GetMapping("/board/list/category-district/{categoryId}/{districtId}/{page}")
    public ResponseEntity<?> boardListInquiryByCategoryAndDistrict(@PathVariable int categoryId,
                                                                   @PathVariable int districtId,
                                                                   @PathVariable int page) {

        return boardService.boardListInquiryByCategoryAndDistrict(categoryId, districtId, page);
    }

    /*
    게시글 리스트 제목으로 검색
     */
    @GetMapping("/board/search-title/{page}")
    public ResponseEntity<?> boardListInquiryByTitle(@RequestParam(name = "title") String title,
                                                     @PathVariable int page) {

        return boardService.boardListInquiryByTitle(title, page);
    }

    /*
    게시글 리스트 제목 + 카테고리로 검색
     */
    @GetMapping("/board/search-title-category/{categoryId}/{page}")
    public ResponseEntity<?> boardListInquiryByTitleAndCategory(@RequestParam(name = "title") String title,
                                                                @PathVariable int categoryId,
                                                                @PathVariable int page) {

        return boardService.boardListInquiryByTitleAndCategory(title, categoryId, page);
    }

    /*
    게시글 리스트 제목 + 지역으로 검색
     */
    @GetMapping("/board/search-title-district/{districtId}/{page}")
    public ResponseEntity<?> boardListInquiryByTitleAndDistrict(@RequestParam(name = "title") String title,
                                                                @PathVariable int districtId,
                                                                @PathVariable int page) {

        return boardService.boardListInquiryByTitleAndDistrict(title, districtId, page);
    }

    /*
    게시글 리스트 제목 + 카테고리 + 지역으로 검색
     */
    @GetMapping("/board/search-title-category-district/{categoryId}/{districtId}/{page}")
    public ResponseEntity<?> boardListInquiryByTitleAndCategoryAndDistrict(@RequestParam(name = "title") String title,
                                                                           @PathVariable int categoryId,
                                                                           @PathVariable int districtId,
                                                                           @PathVariable int page) {

        return boardService.boardListInquiryByTitleAndCategoryAndDistrict(title, categoryId, districtId, page);
    }
}
