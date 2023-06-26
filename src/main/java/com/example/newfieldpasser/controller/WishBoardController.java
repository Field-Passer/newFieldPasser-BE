package com.example.newfieldpasser.controller;

import com.example.newfieldpasser.dto.WishBoardDTO;
import com.example.newfieldpasser.service.WishBoardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class WishBoardController {

    private final WishBoardService wishBoardService;

    /*
    관심 글 등록
     */
    @PostMapping("/board/like")
    public ResponseEntity<?> likeBoard(@RequestBody WishBoardDTO.WishBoardReqDTO wishBoardReqDTO) {

        return wishBoardService.likeBoard(wishBoardReqDTO);
    }

}
