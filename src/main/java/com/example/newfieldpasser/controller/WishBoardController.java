package com.example.newfieldpasser.controller;

import com.example.newfieldpasser.dto.WishBoardDTO;
import com.example.newfieldpasser.service.WishBoardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
public class WishBoardController {

    private final WishBoardService wishBoardService;

    /*
    관심 글 등록
     */
    @PostMapping("/board/register/wish-list")
    public ResponseEntity<?> likeBoard(@RequestBody WishBoardDTO.WishBoardReqDTO wishBoardReqDTO) {

        return wishBoardService.likeBoard(wishBoardReqDTO);
    }

    /*
    관심 글 조회
     */
    @GetMapping("/my-page/wish-list/{page}")
    public ResponseEntity<?> wishList(@PathVariable int page,
                                      Authentication authentication) {

        return wishBoardService.wishList(page, authentication);
    }

    /*
    관심 글 삭제
     */
    @DeleteMapping("/board/delete/wish-list")
    public ResponseEntity<?> deleteWishBoard(@RequestBody WishBoardDTO.WishBoardReqDTO wishPostReqDTO) {

        return wishBoardService.deleteWishBoard(wishPostReqDTO);
    }

}
