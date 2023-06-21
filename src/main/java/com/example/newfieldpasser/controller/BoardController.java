package com.example.newfieldpasser.controller;

import com.example.newfieldpasser.dto.BoardDTO;
import com.example.newfieldpasser.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
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
}
