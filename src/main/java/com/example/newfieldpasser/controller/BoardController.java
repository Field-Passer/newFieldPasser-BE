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
    public ResponseEntity<?> registerBoard(@PathVariable long boardId) {
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
}
