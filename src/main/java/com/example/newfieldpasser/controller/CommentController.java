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
    public ResponseEntity<?> registerComment(Authentication authentication, @RequestBody CommentDTO.CommentReqDTO commentReqDTO){
        return commentService.registerComment(authentication,commentReqDTO);
    }

    /*
    댓글 수정
     */
    @PutMapping("/comment/edit/{commentId}")
    public ResponseEntity<?> updateComment(@PathVariable long commentId, @RequestBody CommentDTO.CommentUpdateDTO commentUpdateDTO){
        return commentService.updateComment(commentId,commentUpdateDTO);
    }

    /*
    댓글 삭제
     */
    @DeleteMapping("/comment/delete/{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable long commentId){
        return commentService.deleteComment(commentId);
    }

     /*
    댓글 조회
     */
    @GetMapping("/comment-lookup/{boardId}/{page}")
    public ResponseEntity<?> commentListInquiryByBoard(@PathVariable long boardId,
                                                       @PathVariable int page,
                                                       Authentication authentication){
        return commentService.commentListInquiryByBoard(boardId,page,authentication);
    }


    /*
     내가 단 댓글 조회
     */
    @GetMapping("/select/memberInquiry/{memberNickName}")
    public ResponseEntity<?> commentByMemberInquiry(@PathVariable String memberNickName){
        return commentService.commentByMemberInquiry(memberNickName);
    }



    @GetMapping("/comment/my-inquiry/{page}")
    public ResponseEntity<?> commentListMember(Authentication authentication, @PathVariable int page){
        return commentService.commentListMember(authentication,page);
    }




}
