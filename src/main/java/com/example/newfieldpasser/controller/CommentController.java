package com.example.newfieldpasser.controller;


import com.example.newfieldpasser.dto.CommentDTO;
import com.example.newfieldpasser.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    /*
    댓글 등록
     */

    @PostMapping("/comment/write")
    public ResponseEntity<?> registerComment(Authentication authentication, @RequestBody CommentDTO.commentReqDTO commentReqDTO){
        return commentService.registerComment(authentication,commentReqDTO);
    }

    /*
    댓글 수정
     */
    @PutMapping("/comment/edit/{commentId}")
    public ResponseEntity<?> updateComment(@PathVariable long commentId, @RequestBody CommentDTO.commentUpdateDTO commentUpdateDTO){
        return commentService.updateComment(commentId,commentUpdateDTO);
    }

    /*
    댓글 삭제
     */
    @DeleteMapping("/comment/delete/{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable long commentId){
        return commentService.deleteComment(commentId);
    }
}
